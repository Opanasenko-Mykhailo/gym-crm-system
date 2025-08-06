package com.gcs.app.repository;

import com.gcs.app.model.Role;
import com.gcs.app.model.enums.RoleType;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RoleRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DataSet(value = "dataset/role-data.xml", cleanBefore = true, cleanAfter = true)
    void findByRoleType_existingRole_returnsRole() {
        RoleType roleType = RoleType.ROLE_TRAINEE;

        Optional<Role> result = roleRepository.findByRoleType(roleType);

        assertTrue(result.isPresent());
        assertEquals(roleType, result.get().getRoleType());
    }

    @Test
    @DataSet(value = "dataset/role-data.xml", cleanBefore = true, cleanAfter = true)
    void findByRoleType_absentInDb_returnsEmptyOptional() {
        RoleType roleType = RoleType.ROLE_TRAINER;

        Optional<Role> result = roleRepository.findByRoleType(roleType);

        assertFalse(result.isPresent());
    }
}
