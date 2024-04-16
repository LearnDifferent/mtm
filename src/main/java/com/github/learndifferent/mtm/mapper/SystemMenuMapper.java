package com.github.learndifferent.mtm.mapper;

import com.github.learndifferent.mtm.entity.SysMenu;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * System Menu Mapper
 *
 * @author zhou
 * @date 2024/4/12
 */
@Repository
public interface SystemMenuMapper {

    /**
     * Get all menus
     *
     * @return menus
     */
    List<SysMenu> getAllMenus();
}
