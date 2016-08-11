package com.eudemon.taurus.app.dao.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.eudemon.taurus.app.dao.UserDao;
import com.eudemon.taurus.app.entites.User;

@Repository
public class UserDaoImpl extends SpringJdbcBaseDao<User> implements UserDao {
	private Logger logger =  LoggerFactory.getLogger(UserDaoImpl.class);

	@Override
	public User rsToObj(ResultSet rs) throws SQLException {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		user.setPasswordEncrypt(rs.getString("password_encrypt"));
		user.setSalt(rs.getString("salt"));
		user.setRoles(rs.getString("roles"));
		user.setPermissions(rs.getString("permissions"));
		user.setRegisterDate(rs.getTimestamp("register_date"));
		user.setPhoto(rs.getString("photo"));
		return user;
	}

	@Override
	public User query(long id) {
		String sql = "select * from user where id=?";
		return queryForObject(sql, id);
	}
	
	@Override
	public long save(final User user) {
		String sql = new String("insert into user(name, password, password_encrypt, salt, roles, permissions) values(?,?,?,?,?,?)");
		long id = saveOnGeneratedKey(sql, user.getName(), user.getPassword(), user.getPasswordEncrypt(), user.getSalt(),
				user.getRoles(), user.getPermissions());
		return id;
	}
	
	@Override
	public boolean delete(long id) {
		String sql = "delete from user where id=?";
		return save(sql, id);
	}
	
	@Override
	public List<User> queryList() {
		String sql = "select * from user";
		return queryForList(sql);
	}
	
	@Override
	public List<User> queryList(int start, int size) {
		String sql = "select * from user order by id asc limit ?,?";
		return queryForList(sql, start, size);
	}
	
	@Override
	public int count() {
		String sql = "select count(*) from user";
		int count = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return count;
	}

	@Override
	public User queryByName(String userName) {
		String sql = "select * from user where name=?";
		return queryForObject(sql, userName);
	}

	@Override
	public boolean update(User t) {
		String sql = "update user set name=?, password=?, password_encrypt=?, salt=?, roles=?, permissions=? where id=?";
		boolean rs = false;
		try {
			rs = this.save(sql, t.getName(), t.getPassword(), t.getPasswordEncrypt(), t.getSalt(), t.getRoles(), t.getPermissions(), t.getId());
		} catch (Exception e) {
			logger.error("user update fail user=" + t, e);
		}
		return rs;
	}
	
	@Override
	public byte[] queryPhoto(final int id) {
		String sql = "select photo from user where id=" + id;
		
		List<byte[]> ls = jdbcTemplate.query(sql, new RowMapper<byte[]>(){
			@Override
			public byte[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				InputStream is = rs.getBinaryStream("photo");
				if(null == is){
					return null;
				}
				try {
					byte[] picBt = IOUtils.toByteArray(is);
					return picBt;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			
		});
		
		if(ls != null && ls.size() > 0){
			return ls.get(0);
		}
		
		return null;
	}
	
	@Override
	public void updatePhoto(final int id, final InputStream inputStream, final long size) {
		String sql = "update user set photo=? where id=?";
		try{
			this.jdbcTemplate.update(sql, new PreparedStatementSetter(){
				public void setValues(PreparedStatement pst) throws SQLException {
					pst.setBinaryStream(1, inputStream, size);
					pst.setInt(2, id);
				}
			});
		}catch(Exception e){
		    logger.error("update user photo fail, id:" + id, e);
		}finally{
		    
		}
	}
}