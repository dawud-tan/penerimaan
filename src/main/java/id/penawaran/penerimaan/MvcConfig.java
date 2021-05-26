package id.penawaran.penerimaan;

import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

@Configuration
public class MvcConfig extends DelegatingWebMvcConfiguration  {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/").resourceChain(true).addResolver(new EncodedResourceResolver());
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/").setCachePeriod(31556926).resourceChain(true).addResolver(new EncodedResourceResolver());
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/").setCachePeriod(31556926).resourceChain(true).addResolver(new EncodedResourceResolver());
        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/static/fonts/").setCachePeriod(31556926).resourceChain(true).addResolver(new EncodedResourceResolver());
        registry.addResourceHandler("/apk/**").addResourceLocations("classpath:/static/apk/").setCachePeriod(31556926).resourceChain(true).addResolver(new EncodedResourceResolver());
	}
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/fonts/**").allowedMethods("GET");
    }

    @Bean
    public LocaleResolver localeResolver(){
       FixedLocaleResolver l = new FixedLocaleResolver();
       Locale indonesia = new Locale.Builder().setLanguage("id").setScript("Latn").setRegion("ID").build();
       l.setDefaultLocale(indonesia);
       return l;
    }

}