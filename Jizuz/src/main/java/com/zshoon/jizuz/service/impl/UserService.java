package com.zshoon.jizuz.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zshoon.jizuz.common.utils.DateUtil;
import com.zshoon.jizuz.dao.UserMapper;
import com.zshoon.jizuz.entity.dto.PermissionDto;
import com.zshoon.jizuz.entity.dto.RoleDto;
import com.zshoon.jizuz.entity.dto.UserDto;
import com.zshoon.jizuz.entity.po.PermissionPo;
import com.zshoon.jizuz.entity.po.RolePo;
import com.zshoon.jizuz.entity.po.UserPo;
import com.zshoon.jizuz.entity.po.UserRolePo;
import com.zshoon.jizuz.service.IUserService;

@Service
public class UserService implements IUserService {

	@Resource
	private UserMapper mapper;

	@Override
	public List<UserDto> findUsers() {
		List<UserDto> dtoList = new ArrayList<>();
		List<UserPo> poList = mapper.findUsers();
		for (UserPo userPo : poList) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userPo, userDto);

			String birthStr = DateUtil.date2String(userPo.getBirthday(), DateUtil.DATE_FORMAT_1);
			userDto.setBirthday(birthStr);
			dtoList.add(userDto);
		}
		return dtoList;
	}

	@Override
	public UserDto findUserByUserName(String username) {
		UserPo userPo = mapper.findByUserName(username);
		UserDto dto = copyPo2Dto(userPo);

		return dto;
	}

	@Override
	public UserDto findUserBuUid(int uid) {
		UserPo po = mapper.findByUid(uid);
		UserDto dto = copyPo2Dto(po);
		return dto;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 36000, rollbackFor = Exception.class)
	public void updateUserAndRole(UserPo userPo, RolePo rolePo) {
		mapper.updateUserByUid4Edit(userPo);
		UserRolePo po = new UserRolePo();
		po.setRid(rolePo.getRid());
		po.setUid(userPo.getUid());
		mapper.updateUserRoleByUid4Edit(po);
	}

	/**
	 * 〈功能详细描述〉
	 *
	 * @param userPo
	 *            UserPo
	 * @return UserDto
	 * @throws BeansException
	 * @author Jizuz
	 * @since v1.0.0
	 */
	private UserDto copyPo2Dto(UserPo userPo) throws BeansException {
		UserDto dto = new UserDto();
		BeanUtils.copyProperties(userPo, dto);

		RolePo rolePo = userPo.getRole();
		RoleDto roleDto = new RoleDto();
		BeanUtils.copyProperties(rolePo, roleDto);

		Set<PermissionDto> permissions = new HashSet<>();
		Set<PermissionPo> pPos = rolePo.getPermissions();
		for (PermissionPo pPo : pPos) {
			PermissionDto permissionDto = new PermissionDto();
			BeanUtils.copyProperties(pPo, permissionDto);
			permissions.add(permissionDto);
		}

		roleDto.setPermissions(permissions);
		String birthStr = DateUtil.date2String(userPo.getBirthday(), DateUtil.DATE_FORMAT_1);
		dto.setBirthday(birthStr);
		dto.setRole(roleDto);
		return dto;
	}

}
