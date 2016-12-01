package coffee.synyx.autoconfigure.discovery.config.global;

import coffee.synyx.autoconfigure.discovery.config.development.DevelopmentCoffeeNetServiceDiscoveryConfiguration;
import coffee.synyx.autoconfigure.discovery.config.integration.IntegrationCoffeeNetServiceDiscoveryConfiguration;
import coffee.synyx.autoconfigure.security.config.global.CoffeeNetSecurityConfiguration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Discovery Configuration.
 *
 * @author  Tobias Schneider - schneider@synyx.de
 */
@Configuration
@Import(
    { IntegrationCoffeeNetServiceDiscoveryConfiguration.class, DevelopmentCoffeeNetServiceDiscoveryConfiguration.class }
)
@AutoConfigureAfter(CoffeeNetSecurityConfiguration.class)
public class CoffeeNetDiscoveryConfiguration {
}