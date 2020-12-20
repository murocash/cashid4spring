package cash.muro.springsecurity.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class DefaultAuthoritiesService implements AuthoritiesService {

	public static final String DEFAULT_ROLE = "ROLE_USER";
	public static final String ADMIN_ROLE = "ROLE_ADMIN";
	
	private final Set<String> admins;
	
	public DefaultAuthoritiesService(Set<String> admins) {
		this.admins = admins;
	}
	
	@Override
	public Collection<GrantedAuthority> authorities(String userId) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		if (admins.contains(userId)) {
			authorities.add(new SimpleGrantedAuthority(ADMIN_ROLE));
		}
		authorities.add(new SimpleGrantedAuthority(DEFAULT_ROLE));
		return authorities;
	}

}
