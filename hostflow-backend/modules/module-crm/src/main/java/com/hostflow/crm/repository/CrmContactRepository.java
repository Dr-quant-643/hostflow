package com.hostflow.crm.repository;

import com.hostflow.crm.entity.CrmContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CrmContactRepository extends JpaRepository<CrmContact, UUID> {
    
    List<CrmContact> findByTenantId(UUID tenantId);
    
    List<CrmContact> findByTenantIdAndStatus(UUID tenantId, CrmContact.ContactStatus status);
    
    List<CrmContact> findByEmail(String email);
    
    boolean existsByEmailAndTenantId(String email, UUID tenantId);
    
    List<CrmContact> findByFullNameContainingIgnoreCaseAndTenantId(String fullName, UUID tenantId);
    
    @Query("SELECT c FROM CrmContact c WHERE c.tenantId = :tenantId AND " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<CrmContact> searchContacts(@Param("tenantId") UUID tenantId, @Param("searchTerm") String searchTerm);
}
