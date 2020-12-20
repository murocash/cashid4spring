package cash.muro.springsecurity.authentication.cashid.conf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.emiperez.commons.idgenerators.DateTimeIdGenerator;

import cash.muro.bch.rpc.client.BchnRpcClient;

@Configuration
public class BchConf {

	@Bean
	public BchnRpcClient getBchClient() throws IOException, URISyntaxException {
		InputStream inputStream = BchConf.class.getClassLoader().getResourceAsStream("bch.properties");
		Properties prop = new Properties();
		prop.load(inputStream);
		BchnRpcClient client = new BchnRpcClient.Builder(new URI(prop.getProperty("node"))).userName(prop.getProperty("username"))
				.password(prop.getProperty("password")).idGenerator(new DateTimeIdGenerator()).build();
		return client;
	}

}
