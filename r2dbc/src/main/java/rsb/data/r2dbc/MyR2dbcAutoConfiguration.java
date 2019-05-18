package rsb.data.r2dbc;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

@Log4j2
@Configuration
@EnableTransactionManagement
@AutoConfigureBefore(ConnectionFactoryAutoConfiguration.class)
public class MyR2dbcAutoConfiguration {

	@Bean
	CustomerService customerService(SimpleCustomerRepository scr,
			TransactionalOperator to) {
		return new CustomerService(scr, to);
	}

	// todo ConnectionFactories.get (URL)
	/*
	 * @Bean ConnectionFactory connectionFactory(@Value("${spring.r2dbc.url}") String url)
	 * { var prefix = "r2dbc:"; if (url.startsWith(prefix)) { url =
	 * url.substring(prefix.length()); } var nUrl = URI.create(url); var host =
	 * nUrl.getHost(); var usrInfo = nUrl.getUserInfo(); var user = ""; var pw = ""; if
	 * (usrInfo != null) { if (usrInfo.contains(":")) { var parts = usrInfo.split(":");
	 * user = parts[0]; pw = parts[1]; } else { user = usrInfo; } } var db =
	 * nUrl.getPath().substring(1); var port = nUrl.getPort(); if (log.isDebugEnabled()) {
	 * log.debug("user: " + user); log.debug("pw: " + pw); log.debug("host: " + host);
	 * log.debug("db name: " + db); log.debug("url: " + nUrl); } var config =
	 * PostgresqlConnectionConfiguration.builder().host(host).database(db) .port(port); if
	 * (StringUtils.hasText(user)) config.username(user); if (StringUtils.hasText(pw))
	 * config.password(pw);
	 *
	 * return (new PostgresqlConnectionFactory(config.build())); }
	 */

	@Bean
	ConnectionFactory connectionFactory(@Value("${spring.r2dbc.url}") String url) {
		return ConnectionFactories.get(url);
	}

}
