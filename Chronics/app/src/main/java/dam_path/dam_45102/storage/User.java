package dam_path.dam_45102.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String email;

    private int socialPoints;

    private ArrayList<String> groupsConnected;

    private Map<String,ArrayList<String>> conversations;

    private ArrayList<Long> publications;

    //tem de ter para ler os dados dele
    public User() {

    }

    public User(String email) {
        this.email = email;

        socialPoints = 0;
        groupsConnected = new ArrayList<String>();
        conversations = new HashMap<String,ArrayList<String>>();
        publications = new ArrayList<Long>();
    }

    public String getEmail(){
        return this.email;
    }

    public void setSocialPoints(int points){
        this.socialPoints = points;
    }

    public int getSocialPoints(){
        return this.socialPoints;
    }

    public void addGroupToUser(String groupName){
        groupsConnected.add(groupName);
    }

    public ArrayList<String> getGroupsConnected(){
        return this.groupsConnected;
    }

    public void addConversation(String otherUser, ArrayList<String> conversationAdded){
        this.conversations.put(otherUser, conversationAdded);
    }

    public void addPublicationId(long id){
        publications.add(id);
    }

    public ArrayList<Long> getPublications(){
        return this.publications;
    }


}
