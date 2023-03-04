package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception {
        //userMobile.add(mobile);
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        if(users.size() == 2){
            Group group = new Group(users.get(1).getName(), 2);
            groupUserMap.put(group, users);
            return group;
        }
        Group group = new Group("Group" + ++customGroupCount, users.size());
        groupUserMap.put(group, users);
        return group;
    }

    public int createMessage(String content) {
        Message message = new Message(++messageId, content);
        message.setTimestamp(new Date());

        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        boolean flag = false;
        for(User user : groupUserMap.get(group)){
            if(user.equals(sender)){
                flag = true;
                break;
            }
        }
        if(flag = false){
            throw new Exception("You are not allowed to send message");
        }
        if(groupMessageMap.containsKey(group)){
            groupMessageMap.get(group).add(message);
        }
        else{
            List<Message> list = new ArrayList<>();
            list.add(message);
            groupMessageMap.put(group, list);
        }
        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception{
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!adminMap.get(group).equals(approver)){
            throw new Exception("Approver does not have rights");
        }
        boolean flag = false;
        for(User user1 : groupUserMap.get(group)){
            if(user1.equals(user)){
                flag = true;
                break;
            }
        }
        if(flag == false){
            throw new Exception("User is not a participant");
        }
        adminMap.put(group,user);
        //User newAdin = null;
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception {
        boolean flag = false;


//        for(List<User> list : groupUserMap.values()){
//            for(User user1 : list){
//                if(user1.equals(user)){
//
//                    flag = true;
//                    break;
//                }
//            }
//        }
//        if(flag == false){
//            throw new Exception("User not found");
//        }


        for(Group group : groupUserMap.keySet()){
            for(User user1 : groupUserMap.get(group)){
                if(user1.equals(user)){
                    if(adminMap.get(group).equals(user)){
                        throw new Exception("Cannot remove admin");
                        break;
                    }
                    flag = true;
                    List<User> list = groupUserMap.get(group);
                    Message msg = senderMap.getKey(user);
                    list.remove(user);
                }
            }
        }
        if(flag == false){
            throw new Exception("User not found");
        }



    }
}
