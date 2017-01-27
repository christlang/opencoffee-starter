package coffee.synyx.autoconfigure.security.config;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.http.MediaType;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import javax.servlet.Filter;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_XHTML_XML;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import static java.util.Collections.singleton;


/**
 * @author  Tobias Schneider - schneider@synyx.de
 */
@EnableConfigurationProperties(CoffeeNetSecurityProperties.class)
public class IntegrationCoffeeNetWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    private static final String LOGIN = "/login";
    private static final String LOGOUT = "/logout";

    private CoffeeNetSecurityProperties securityConfigurationProperties;
    private UserInfoTokenServices userInfoTokenServices;
    private CoffeeNetSecurityResourceProperties coffeenetResource;
    private OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        enableSso(http).authorizeRequests().anyRequest().authenticated().and().csrf().disable();
    }


    /**
     * Configures the passed {@link HttpSecurity} to support oauth2 sso. This Method should be used to enable oauth2 sso
     * if this config is extended. <b>This method does not configure any security constraints for any requests.</b>
     *
     * @param  http  The {@link HttpSecurity} that should be configured to support oauth2 sso.
     *
     * @return  The configured {@link HttpSecurity}.
     *
     * @throws  Exception  Thrown if an exception occurs during configuration.
     */
    public HttpSecurity enableSso(HttpSecurity http) throws Exception {

        return http.logout()
            .logoutUrl(LOGOUT)
            .logoutSuccessUrl(securityConfigurationProperties.getLogoutSuccessUrl())
            .and()
            .exceptionHandling()
            .defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint(LOGIN),
                    mediaTypeRequestMatcher(http.getSharedObject(ContentNegotiationStrategy.class)))
            .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(UNAUTHORIZED),
                    new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"))
            .and()
            .addFilterBefore(oAuth2ClientAuthenticationProcessingFilter, BasicAuthenticationFilter.class)
            .addFilterBefore(apiTokenAccessFilter(), AbstractPreAuthenticatedProcessingFilter.class);
    }


    private Filter apiTokenAccessFilter() {

        return new CoffeeNetApiTokenAccessFilter(userInfoTokenServices, coffeenetResource);
    }


    private static MediaTypeRequestMatcher mediaTypeRequestMatcher(
        final ContentNegotiationStrategy contentNegotiationStrategy) {

        ContentNegotiationStrategy negotiationStrategy = contentNegotiationStrategy;

        if (negotiationStrategy == null) {
            negotiationStrategy = new HeaderContentNegotiationStrategy();
        }

        MediaTypeRequestMatcher matcher = new MediaTypeRequestMatcher(negotiationStrategy, APPLICATION_XHTML_XML,
                new MediaType("image", "*"), TEXT_HTML, TEXT_PLAIN);
        matcher.setIgnoredMediaTypes(singleton(ALL));

        return matcher;
    }


    @Autowired
    public void setSecurityConfigurationProperties(CoffeeNetSecurityProperties securityConfigurationProperties) {

        this.securityConfigurationProperties = securityConfigurationProperties;
    }


    @Autowired
    public void setUserInfoTokenServices(UserInfoTokenServices userInfoTokenServices) {

        this.userInfoTokenServices = userInfoTokenServices;
    }


    @Autowired
    public void setCoffeenetResource(CoffeeNetSecurityResourceProperties coffeenetResource) {

        this.coffeenetResource = coffeenetResource;
    }


    @Autowired
    public void setoAuth2ClientAuthenticationProcessingFilter(
        OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter) {

        this.oAuth2ClientAuthenticationProcessingFilter = oAuth2ClientAuthenticationProcessingFilter;
    }
}