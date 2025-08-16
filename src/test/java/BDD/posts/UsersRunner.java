package BDD.posts;
import com.intuit.karate.junit5.Karate;

public class UsersRunner {
    @Karate.Test
    Karate test() {
        return Karate.run("Users.feature"
        ).relativeTo(getClass());
    }
}