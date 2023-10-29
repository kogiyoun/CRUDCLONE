package miniproject.demo.dao;

import miniproject.demo.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.*;


//Spring이 관리하는 bean
@Repository
public class UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsertOperations insertUser;

    public UserDao(DataSource dataSource){
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("user")
                .usingGeneratedKeyColumns("user_id"); // 자동으로 증가되는 id를 설정.
    }
    // Spring JDBC를 이용한 코드.
    @Transactional
    public User addUser(String email, String name, String password){
        //serivce에서 이미 트랜잭션이 시작했기 때문에 그 트랜잭션에 포함되는 개념임 새로 만들어지는게 아님
        //   insert into user(email,name,password,regdate) values(?, ?, ?, now());
        //   select last_insert_id();
        User user = new User();
        user.setName(name); // name 칼럼.
        user.setEmail(email); // email
        user.setPassword(password); // password
        user.setRegdate(String.valueOf(now()));
        SqlParameterSource params = new BeanPropertySqlParameterSource(user);
        Number number = insertUser.executeAndReturnKey(params);// insert를 실행하고, 자동으로 생성된 id를 가져온다.
        int userId = number.intValue();
        user.setUserId(userId);
        return user;
    }

    @Transactional
    public void mappingUserRole(int userId){
        //serivce에서 이미 트랜잭션이 시작했기 때문에 그 트랜잭션에 포함되는 개념임 새로 만들어지는게 아님
        //insert into user_role(user_id, role_id) values(?, 1);
        //이번엔 simplejdbc사용하지않고 만들어보기
        String sql = "insert into user_role( user_id, role_id ) values (:userId, 1)";
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        jdbcTemplate.update(sql, params);
    }

    @Transactional
    public User getUser(String email) {
        try {
            // user_id => setUserId , email => setEmail ...
            String sql = "select user_id, email, name, password, regdate from user where email = :email";
            SqlParameterSource params = new MapSqlParameterSource("email", email);
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
            User user = jdbcTemplate.queryForObject(sql, params, rowMapper);
            return user;
        }catch(Exception ex){
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        String sql = "select r.name from user_role ur, role r where ur.role_id = r.role_id and ur.user_id = :userId";

        List<String> roles = jdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) -> {
            return rs.getString(1);
        });
        return roles;
    }
}/*
   insert into user(email,name,password,regdate) values(?, ?, ?, now()); #user_id는 auto_increment
   select last_insert_id();
   insert into user_role(user_id, role_id) values(?, 1); # ?는 자동으로 생성된 user_id가들어가는것이고 1은 role_id의 role_user값
 */
