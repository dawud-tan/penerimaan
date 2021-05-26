package id.penawaran.penerimaan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import static java.util.Map.entry;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedParser;
import org.bouncycastle.math.ec.rfc7748.X25519Field;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.encoders.Base64;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

//@GetMapping("/{tahun}/{bulan}/{tanggal}/{judulPost}")
@Controller
public class JAXMServlet {//implements javax.xml.messaging.ReqRespListener

    private static SMIMESignedGenerator gen;
    private static MessageDigest md;
    private static SignerInformationVerifier siv;
    private static DigestCalculatorProvider dcp;
	private static HashMap<String, X509Certificate> guidKP = new HashMap<>();

    static {
        Security.addProvider(new BouncyCastleProvider());
        //i don't know wether this the correct way to load trading partner key
        try {
            md = MessageDigest.getInstance(NISTObjectIdentifiers.id_sha512.getId(), "BC");//perlu canonicalize
            //---------------begin server keys-------------------
            //---------------begin server private-------------------
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            InputStream eis = cl.getResourceAsStream("offeree.crt");
            X509Certificate recipientRSAPublicKey = PemUtils.decodeCertificate(eis);
            eis.close();

            eis = cl.getResourceAsStream("offeree.key");
            PrivateKey recipientRSAPrivateKey = PemUtils.decodePrivateKey(eis, "RSA");
            eis.close();

            gen = new SMIMESignedGenerator();
            SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder()
                    .setProvider("BC")
                    .build("sha512withRSA", recipientRSAPrivateKey, recipientRSAPublicKey);
            gen.setContentTransferEncoding("binary");
            gen.addSignerInfoGenerator(signer);
            //---------------end server private-------------------
            //---------------end server keys-------------------

            //---------------begin client keys-------------------
            //---------------begin client public-------------------
            File f = new File("./sertifikat");
            String[] paths = f.list();
            if(paths != null)
            for(int jml = 0; jml<paths.length; jml++){
				Path filePath = Paths.get("./sertifikat/"+paths[jml]);
				String content = Files.readString(filePath);
				X509Certificate kunciPublik = DerUtils.decodeCertificate(new ByteArrayInputStream(Base64.decode(content)));
				guidKP.put(filePath.getFileName().toString().replace(".b64cert",""), kunciPublik);
		    }
            dcp = new JcaDigestCalculatorProviderBuilder().setProvider("BC").build();
            //---------------end client public-------------------
            //---------------end client keys-------------------
        } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | CertificateException | InvalidKeySpecException | OperatorCreationException ex) {
            ex.printStackTrace();
        }
    }

    private List<Pesan> penawarans = new ArrayList<>();
    private Locale indonesia = new Locale.Builder().setLanguage("id").setScript("Latn").setRegion("ID").build();
    private DateTimeFormatterBuilder builder;
	private DateTimeFormatter polaTanggalIso8601 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").withLocale(indonesia);
	private Map<Integer, Integer> bulanBiasa = Map.ofEntries(entry(1, 0), entry(2, 4), entry(3, 1), entry(4, 0), entry(5, 0), entry(6, 4), entry(7, 4), entry(8, 3), entry(9, 2), entry(10, 2), entry(11, 1), entry(12, 1));
	private Map<Integer, Integer> bulanKabisat = Map.ofEntries(entry(1, 1), entry(2, 0), entry(3, 1), entry(4, 0), entry(5, 0), entry(6, 4), entry(7, 4), entry(8, 3), entry(9, 2), entry(10, 2), entry(11, 1), entry(12, 1));
	private Map<Integer, String> pasaran = Map.ofEntries(entry(0, "Legi"), entry(1, "Pahing"), entry(2, "Pon"), entry(3, "Wage"), entry(4, "Kliwon"));
	private ZoneId jakarta = ZoneId.of("Asia/Jakarta");

    public JAXMServlet() {
    }

    @GetMapping("/")
    public String index(@ModelAttribute("model") ModelMap model) {
		ZonedDateTime wib = ZonedDateTime.now(jakarta);
		int tahun = wib.getYear();
		boolean kabisat = (tahun & 3) == 0 && ((tahun % 25) != 0 || (tahun & 15) == 0);
		int jumlahKabisat = tahun/4 - tahun/100*25;
		int tanggal = wib.getDayOfMonth();
		int bulan = 	kabisat ? bulanKabisat.get(wib.getMonthValue()) : bulanBiasa.get(wib.getMonthValue());
		builder = new DateTimeFormatterBuilder();
		DateTimeFormatter polaTanggal = builder
		    .appendPattern("eeee")
		    .appendLiteral(" ")
		    .appendLiteral(pasaran.get((jumlahKabisat + tanggal - bulan)%5))
		    .appendLiteral(", ")
		    .appendPattern("dd MMMM yyyy, HH:mm:ss z")
		    .toFormatter(indonesia);
		String waktu = wib.format(polaTanggal);
		String datetime = wib.format(polaTanggalIso8601);
        model.addAttribute("penawarans", penawarans)
        .addAttribute("waktu", waktu)
        .addAttribute("datetime", datetime);
        return "index";
    }

    @PostMapping(path = "/daftarkan-kunci-publik-pengguna", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void simpanSertifikat(@RequestParam MultiValueMap<String,String> paramMap) throws Exception {
		String guid = paramMap.getFirst("guid");
		String kunciPublik = paramMap.getFirst("kunciPublik");
		
		//simpan id_pengguna DAN kunci publik
		X509Certificate senderRSAPublicKey = DerUtils.decodeCertificate(new ByteArrayInputStream(Base64.decode(kunciPublik)));
		guidKP.put(guid, senderRSAPublicKey);
		
		String namaFile = "./sertifikat/"+ guid + ".b64cert";
		File yourFile = new File(namaFile);
		yourFile.createNewFile();
		Path filePath = yourFile.toPath();
		Files.writeString(filePath, kunciPublik);
	}

    @PostMapping(value = "/pembentukan-perikatan-elektronik", consumes = "multipart/signed", produces = "multipart/signed")
    @ResponseBody
    public void onMessage(HttpEntity<byte[]> message, HttpServletRequest request, HttpServletResponse response) throws Exception {
        InternetHeaders ih = new InternetHeaders();
        HttpHeaders hh = message.getHeaders();
        if (hh.containsKey("Content-Type")) {
            ih.addHeader("Content-Type", hh.get("Content-Type").get(0));
        }
        MimeBodyPart bodyPart = new MimeBodyPart(ih, message.getBody());
        X509Certificate senderRSAPublicKey = guidKP.get(hh.get("AS2-From").get(0));
        siv = new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(senderRSAPublicKey);        
        String calculatedMIC = "";
        String micAlg = hh.get("Disposition-Notification-Options").get(0).split("; ", -1)[1].split(", ", -1)[1];
        boolean hasilVer = false;
        try {
            MimeMultipart signedBodyPart = (MimeMultipart) bodyPart.getContent();
            SMIMESignedParser smp = new SMIMESignedParser(dcp, signedBodyPart);
            SignerInformation signer = smp.getSignerInfos().getSigners().iterator().next();
            hasilVer = signer.verify(siv);
            //RFC 3852 5.4 https://tools.ietf.org/html/rfc3852#section-5.4
            //sig must be composed from the DER encoding
            byte[] ttdnya = signer.getSignature();
            BigInteger res = new BigInteger(1, ttdnya);

            AttributeTable table = signer.getSignedAttributes();

            ASN1Set attrValues1 = table.get(CMSAttributes.messageDigest).getAttrValues();
            ASN1Primitive validMessageDigest = attrValues1.getObjectAt(0).toASN1Primitive();
            DEROctetString signedMessageDigest = (DEROctetString) validMessageDigest;
            String hashPesan = new String(Base64.encode(signedMessageDigest.getOctets()), "utf8");

            ASN1Set attrValues = table.get(CMSAttributes.signingTime).getAttrValues();
			ASN1Primitive validSigningTime = attrValues.getObjectAt(0).toASN1Primitive();
			Time signingTime = Time.getInstance(validSigningTime);
			Date tangPen = signingTime.getDate();

            System.out.println("is offeror authentic?: " + hasilVer);
            String receivedMic = new String(Base64.encode(signer.getContentDigest()));//bagaimana received ditentukan?
            MimeBodyPart pesanPembeli = (MimeBodyPart) signedBodyPart.getBodyPart(0);
            calculatedMIC = calculateMIC(pesanPembeli);
            System.out.println("Calculated MIC: " + calculatedMIC);
            System.out.println("Received MIC: " + receivedMic);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            pesanPembeli.getDataHandler().writeTo(baos);
            String pesannya = new String(baos.toByteArray(), "utf-8");
            String[] pairs = StringUtils.tokenizeToStringArray(pesannya, "&");
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx == -1) {
                    System.out.println(URLDecoder.decode(pair, "utf-8"));
                } else {
                    String name = URLDecoder.decode(pair.substring(0, idx), "utf-8");
                    String value = URLDecoder.decode(pair.substring(idx + 1), "utf-8");
                    System.out.println(name + ": " + value);
                    Pesan psn = new Pesan();
                    psn.penawaran = value;
                    psn.sisaBagi = res;
                    psn.tanggal = tangPen;
                    psn.hashnya = hashPesan;
                    psn.alamatIP = request.getRemoteAddr();
                    penawarans.add(psn);
                }
            }
        } catch (IOException | MessagingException | CMSException ex) {
            ex.printStackTrace();
        }

        // Create the report and sub-body parts
        final MimeMultipart aReportParts = new MimeMultipart();
        // Create the text part
        final MimeBodyPart aTextPart = new MimeBodyPart();
        final String sText = hasilVer ? "Pesan dari Anda tiba di komputer offeree utuh.\r\n" : "pesan anda termodifikasi\r\n";
        aTextPart.setContent(sText, MediaType.TEXT_PLAIN_VALUE);
        aTextPart.setHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE);
        aReportParts.addBodyPart(aTextPart);

        final MimeBodyPart aReportPart = new MimeBodyPart();
        {
            final InternetHeaders aReportValues = new InternetHeaders();
            aReportValues.setHeader("Reporting-UA", "Spring Boot 2.4.5");
            aReportValues.setHeader("Original-Recipient", "rfc822; ".concat(hh.get("AS2-To").get(0)));
            aReportValues.setHeader("Final-Recipient", "rfc822; ".concat(hh.get("AS2-To").get(0)));
            aReportValues.setHeader("Original-Message-ID", hh.get("Message-Id").get(0));
            aReportValues.setHeader("Disposition", "automatic-action/MDN-sent-automatically; processed");
            aReportValues.setHeader("Received-Content-MIC", calculatedMIC.concat(", ").concat(micAlg));

            final Enumeration<?> aReportEn = aReportValues.getAllHeaderLines();
            final StringBuilder aReportData = new StringBuilder();
            while (aReportEn.hasMoreElements()) {
                aReportData.append((String) aReportEn.nextElement()).append("\r\n");
            }
            aReportData.append("\r\n");
            aReportPart.setContent(aReportData.toString(), "message/disposition-notification");
        }
        aReportPart.setHeader("Content-Type", "message/disposition-notification");
        aReportParts.addBodyPart(aReportPart);
        // Convert report parts to MimeBodyPart
        final MimeBodyPart aReport = new MimeBodyPart();
        aReportParts.setSubType("report; report-type=disposition-notification");
        aReport.setContent(aReportParts);
        aReport.setHeader("Content-Type", aReportParts.getContentType());

        MimeMultipart aSignedData = gen.generate(aReport);
        final MimeBodyPart aTmpBody = new MimeBodyPart();
        aTmpBody.setContent(aSignedData);
        aTmpBody.setHeader("Content-Type", aSignedData.getContentType());
        response.setContentType(aTmpBody.getContentType());
        aTmpBody.getInputStream().transferTo(response.getOutputStream());
    }

    private String calculateMIC(MimeBodyPart part) {
        try {
            md.reset();
            //https://tools.ietf.org/html/rfc4130#section-7.3.1
            //Canonicalization on the MIME headers MUST be performed before the MIC is calculated
            //Start hashing the header
            final byte[] aCRLF = new byte[]{'\r', '\n'};
            final Enumeration<String> aHeaderLines = part.getAllHeaderLines();
            while (aHeaderLines.hasMoreElements()) {
                String h = aHeaderLines.nextElement();
                md.update(getAllAsciiBytes(h));
                md.update(aCRLF);
            }
            // The CRLF separator between header and content
            md.update(aCRLF);
            // No need to canonicalize here - see issue https://github.com/phax/as2-lib/issues/12
            try (final DigestOutputStream aDOS = new DigestOutputStream(new NullOutputStream(), md);
                    final OutputStream aOS = MimeUtility.encode(aDOS, part.getEncoding())) {
                part.getDataHandler().writeTo(aOS);
            }
            // Build result digest array
            final byte[] aMIC = md.digest();
            // Perform Base64 encoding and append algorithm ID
            return new String(Base64.encode(aMIC));
        } catch (IOException | MessagingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getAllAsciiBytes(final String sString) {
        final char[] aChars = sString.toCharArray();
        final int nLength = aChars.length;
        final byte[] ret = new byte[nLength];
        for (int i = 0; i < nLength; i++) {
            ret[i] = (byte) aChars[i];
        }
        return ret;
    }
}