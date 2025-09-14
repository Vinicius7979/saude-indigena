package com.saude_indigena.repository;

import com.saude_indigena.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query(value = "select admin.usuario from saude.admin admin where admin.usuario = :usuario", nativeQuery = true)
    Optional<Admin> buscarPorUsuario(String usuario);

    UserDetails findByUsuario(String usuario);
}
