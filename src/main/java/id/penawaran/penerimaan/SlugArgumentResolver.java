package id.penawaran.penerimaan;

import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SlugArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return false;
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    String requestPath = ((ServletWebRequest) webRequest).getRequest().getPathInfo();
    String slug = requestPath.substring(0, requestPath.indexOf("/", 1)).replaceAll("^/", "");
    return "dummy";//gitRepositoryFinder.findBySlug(slug).orElseThrow(NotFoundException::new);
  }
}