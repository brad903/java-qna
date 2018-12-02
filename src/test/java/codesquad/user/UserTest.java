package codesquad.user;

import codesquad.utils.Result;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    User user1, user2, user3, user4;

    @Before
    public void setUp() throws Exception {
        user1 = new User(25, "leeh903", "1234", "이정현", "leejh903@naver.com");
        user2 = new User(27, "brad903", "1234", "브래드", "brad903@naver.com");
        user3 = new User(27, "dfadf", "1234", "익명사용자", "dfsadfadsf@naver.com");
        user4 = new User(28, "dfadf", "1234", "익명사용자", "dfsadfadsf@naver.com");
    }

    @Test
    public void 사용자정보_업데이트() {
        user1.update(user2, user1);
        assertThat(user1.getEmail()).isEqualTo(user2.getEmail());
        assertThat(user1.getName()).isEqualTo(user2.getName());
        assertThat(user1.getId()).isEqualTo(user1.getId());
    }

    @Test
    public void 세션유저와_유저와_같지않을때() {
        Result result = user1.update(user2, user3);
        assertThat(result.isValid()).isEqualTo(false);
    }

    @Test
    public void matchId() {
        assertThat(user2.isSameUser(27)).isEqualTo(Result.ok());
    }

    @Test
    public void matchPassword() {
        assertThat(user1.matchPassword("1234")).isEqualTo(true);
    }

    @Test
    public void matchUser() {
        assertThat(user2.equals(user3)).isEqualTo(true);
        assertThat(user3.equals(user4)).isEqualTo(false);
    }
}