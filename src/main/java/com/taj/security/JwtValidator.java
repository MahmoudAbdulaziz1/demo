package com.taj.security;

import com.taj.model.NewLoginModelDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {


    private String secret = "youtube";

    public NewLoginModelDto validate(String token) {

        NewLoginModelDto jwtUser = null;
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            jwtUser = new NewLoginModelDto();
            jwtUser.setUser_email(body.getSubject());
            jwtUser.setLogin_id(Integer.parseInt((String) body.get("userId")));
            jwtUser.setLogin_role((String) body.get("role"));
            jwtUser.setLogin_date((long) body.get("currentDate"));
        } catch (Exception e) {
            System.out.println(e);
        }

        return jwtUser;
    }
}
