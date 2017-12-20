package net.hzf.web.italker.push.service;



import net.hzf.web.italker.push.bean.api.account.RegisterModel;
import net.hzf.web.italker.push.bean.card.UserCard;
import net.hzf.web.italker.push.bean.db.User;
import net.hzf.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

//地址：127.0.1/api/account

/**
 * 用户的Http的入口
 */
@Path("/account")
public class AccountService {


    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserCard register(RegisterModel model){
        User user = UserFactory.findByPhone(model.getAccount().trim());
        if (user != null){
            UserCard userCard = new UserCard();
            userCard.setName("该账号已存在");
            userCard.setModifyAt(user.getUpdateAt());
            return userCard;
        }

        user = UserFactory.findByName(model.getName().trim());
        if (user != null){
            UserCard userCard = new UserCard();
            userCard.setName("该用户名已存在");
            userCard.setModifyAt(user.getUpdateAt());
            return userCard;
        }

        user = UserFactory.register(model.getAccount(),model.getPassword(),model.getName());

        if (user != null){
        UserCard card = new UserCard();
        card.setName(user.getName());
        card.setFollow(true);
        card.setPhone(user.getPhone());
        card.setModifyAt(user.getUpdateAt());
        return card;
        }
        return null;
    }



}
