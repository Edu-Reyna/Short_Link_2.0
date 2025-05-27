package com.example.demo.services.implementation;

import com.example.demo.controller.dto.AuthCreateUserRequest;
import com.example.demo.controller.dto.AuthLoginRequest;
import com.example.demo.controller.dto.AuthResponse;
import com.example.demo.controller.dto.GoogleUserDTO;
import com.example.demo.entities.RoleEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.repositories.IRoleRepository;
import com.example.demo.repositories.IUserRepository;
import com.example.demo.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final JwtUtils jwtUtils;

    private final IUserRepository userRepository;

    private final IRoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("The email: " + username + "does not exist."));

        List<SimpleGrantedAuthority>authorityList = new ArrayList<>();

        userEntity.getRoles()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userEntity.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionEnum().name())));

        String pwd = (userEntity.getPassword() != null) ? userEntity.getPassword() : "";

        return new User(userEntity.getEmail(),
                pwd,
                userEntity.isEnabled(),
                userEntity.isAccountNoExpired(),
                userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),
                authorityList
        );
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {
        String email = authLoginRequest.email();
        String password = authLoginRequest.password();

        Authentication authentication = this.authentication(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity userEntity = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("The email: " + email + "does not exist."));

        String roles = userEntity.getRoles().stream()
                .map(RoleEntity::getRoleEnum).map(Enum::name).collect(Collectors.joining(","));

        String accessToken = jwtUtils.generateToken(authentication, userEntity.getId(), roles);

        return new AuthResponse(email, "User logged in successfully", accessToken, true);
    }

    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest) {

        String username = authCreateUserRequest.name();
        String email = authCreateUserRequest.email();
        String password = authCreateUserRequest.password();

        Optional<RoleEntity> roleEntity = roleRepository.findRoleEntityById(authCreateUserRequest.idRole());

        UserEntity userEntity = UserEntity.builder()
                .name(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .roles(Set.of(roleEntity.get()))
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();

        UserEntity userSaved = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();

        userSaved.getRoles()
                .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userSaved.getRoles().stream().flatMap(role -> role.getPermissionList()
                        .stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getPermissionEnum().name())));

        String roles = userEntity.getRoles().stream().map(RoleEntity::getRoleEnum).map(Enum::name).collect(Collectors.joining(","));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userSaved, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateToken(authentication, userEntity.getId(), roles);

        return new AuthResponse(email, "User created successfully", accessToken, true);
    }

    public AuthResponse googleLogin(OAuth2AuthenticationToken googleToken) {
        GoogleUserDTO googleUserDTO = getGoogleUserDTO(googleToken);

        String email = googleUserDTO.getEmail();
        String name = googleUserDTO.getName();

        Optional<UserEntity> userEntityOptional = userRepository.findUserEntityByEmail(email);

        if (userEntityOptional.isPresent()) {
            Authentication authentication = this.authentication(email);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserEntity userEntity = userEntityOptional.get();

            String roles = userEntity.getRoles().stream()
                    .map(RoleEntity::getRoleEnum).map(Enum::name).collect(Collectors.joining(","));

            String accessToken = jwtUtils.generateToken(authentication, userEntity.getId(), roles);

            return new AuthResponse(email, "User logged in successfully", accessToken, true);
        }

        Optional<RoleEntity> roleEntity = roleRepository.findRoleEntityById(2L);

        UserEntity userEntity = UserEntity.builder()
                .name(name)
                .email(email)
                .roles(Set.of(roleEntity.get()))
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();

        UserEntity userSaved = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();

        userSaved.getRoles()
                .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userSaved.getRoles().stream().flatMap(role -> role.getPermissionList()
                        .stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getPermissionEnum().name())));

        String roles = userEntity.getRoles().stream().map(RoleEntity::getRoleEnum).map(Enum::name).collect(Collectors.joining(","));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userSaved, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateToken(authentication, userEntity.getId(), roles);

        return new AuthResponse(email, "User created successfully", accessToken, true);

    }

    private Authentication authentication(String email, String password) {

        UserDetails userDetails = loadUserByUsername(email);

        if ((userDetails == null)) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }
        return new UsernamePasswordAuthenticationToken(email, password, userDetails.getAuthorities());
    }

    private Authentication authentication(String email) {

        UserDetails userDetails = loadUserByUsername(email);

        if ((userDetails == null)) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
    }

    private GoogleUserDTO getGoogleUserDTO(OAuth2AuthenticationToken googleToken) {

        return GoogleUserDTO.builder()
                .email(googleToken.getPrincipal().getAttribute("email"))
                .name(googleToken.getPrincipal().getAttribute("name"))
                .picture(googleToken.getPrincipal().getAttribute("picture"))
                .sub(googleToken.getPrincipal().getAttribute("sub"))
                .build();
    }
}