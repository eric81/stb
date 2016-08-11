package com.eudemon.taurus.app.action;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eudemon.taurus.app.common.Config;
import com.eudemon.taurus.app.entites.OperResult;
import com.eudemon.taurus.app.entites.PagedEntity;
import com.eudemon.taurus.app.entites.User;
import com.eudemon.taurus.app.service.UserService;
import com.eudemon.taurus.app.utils.RequestUtils;

/**
 * 用户管理Action
 * 
 * @author calvin
 */
@RestController
@RequestMapping(value = "/user")
public class UserAction {
	private Logger logger =  LoggerFactory.getLogger(UserAction.class);
	@Autowired
	private UserService sv;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public User detail(@PathVariable int id) {
		User user = null;
		try {
			user = this.sv.getUserById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
	}

	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	public User query(@PathVariable String name) {
		User user = null;
		try {
			user = this.sv.getUserByName(name);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public PagedEntity<User> list(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int pageSize) {
		logger.info("[user list] page=" + page + " pageSize=" + pageSize);
		System.out.println(Config.getString("name"));
		PagedEntity<User> rs = null;
		try {
			rs = this.sv.getPagedUserList(page, pageSize);
		} catch (Exception e) {
			logger.error("[user list] exception : " + e.getMessage(), e);
		}

		return rs;
	}

	@RequestMapping(value = "/delete/{id}")
	public OperResult delete(@PathVariable int id) {
		OperResult or = new OperResult();
		boolean rs = this.sv.delete(id);
		if (!rs) {
			or.setCode(OperResult.CODE_FAIL);
			or.setMessage("fail");
		}
		
		return or;
	}

	@RequestMapping(value = "/modify/{id}")
	public OperResult modify(@PathVariable int id, @RequestParam String role, @RequestParam String permissions) {
		logger.debug("role=" + role);
		OperResult or = new OperResult();
		try {
			role = URLDecoder.decode(role, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		boolean rs = this.sv.modify(id, role, permissions);
		if (!rs) {
			or.setCode(OperResult.CODE_FAIL);
			or.setMessage("fail");
		}

		return or;
	}

	@RequestMapping(value = "uploadPhoto")
	public String uploadPhoto(HttpServletRequest request, @RequestParam("userfile") MultipartFile file, ModelMap model)
			throws Exception {
		int id = RequestUtils.getParameterAsInt(request, "id", 0);
		String view = RequestUtils.getParameterAndTrim(request, "v");
		String name = RequestUtils.getParameterAndTrim(request, "name");

		logger.trace("UserAction.uploadPhoto.parameter[id=" + id + ",name=" + name + "]");

		sv.updatePhoto(id, file);

		if (null != view && !view.equals("")) {
			return "redirect:" + view;
		} else {
			return "forward:/user/query?name=" + name;
		}
	}

	@RequestMapping(value = "viewPhoto")
	public void viewPhoto(HttpServletRequest request, OutputStream responseBodyOut) throws Exception {
		int id = RequestUtils.getParameterAsInt(request, "id", 0);
		logger.trace("UserAction.viewPhoto.parameter[id=" + id + "]");

		try {
			byte[] photo = sv.getPhoto(id);
			if (photo == null) {
				photo = new byte[] {};
			}
			responseBodyOut.write(photo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
