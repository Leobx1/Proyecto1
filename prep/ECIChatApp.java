import java.util.HashMap;
import java.util.Set;


public class ECIChatApp {
    private HashMap<Integer, Room> rooms;
    private HashMap<Integer, User> users;
    private HashMap<Integer, Message> messages;
    
    public ECIChatApp(){
    this.rooms = new HashMap<Integer, Room> ();
    this.users = new HashMap<Integer,User> ();
}
public void activateSuspendedUsers(){
    for (User u : this.users.values()){
        if (u.getStatus().equals("SUSPENDED")){
            u.setStatus("ACTIVE");        
        }
    }
}
public void closeAllRooms(){
    for(Room r: this.rooms.values()){
        r.close();
        
    }
}
public Message searchMessage(int messageId){
    for (Message m: this.messages.values()){
        if (m.GetId().equals(messageId){
            return m
        }
    return null
    }
    
public void addReaction (String reaction){
   if (!reaction.equals("dedomedio"){
       Reaction = new Reaction(reaction);
       this.reactions.add(reaction);
       
   }
}
public boolean closeChatIfAllowed(int chatId, int userId){
    User u = this.users.get(userId);
    if (u == null){
        return false;
    if (!u.getType().equals("PROFESSOR")){
        return false;
    Chat c = this.chats.get(chatId);
    if (c == null){
        return false;
    if (isClosed() == true){
        return false;
    if (c.getType().equals("GROUP"))
    }
    
    }
        
    }
    
        
    }
    
    
        
        
    }
    c.close();
    return true
    
}
private Room findRoom(int roomId){
    return this.rooms.get(roomId);
}
private User findUser(int userId){
   return this.users.get(userId); 
}
public Chat findChat(int chatId){
    return this.chats.get(chatId);
}

public boolean hasChat(Chat c){
    for (Chat h: this.chats.values()){
        if (h.equals(c)){
            return true;
        }
    }
    return false;
}
public boolean isSameContentAndAuthor(String content, User author){
    if (this.author.equals(author) && this.content.equals(content)){
        return true;
    }
}
public void updateTimestamp(){
    this.timestamp = LocalDateTime.now();
}
private int generateMessageId(){
    return this.messages.size() + 1;
}
private void addMessageToContainer(int messageId, Message newMessage){
    messages.put(messageId, newMessage);
}
public void addMessage(String content, User author){
    for (Message m : this.messages.values()){
        boolean exists = m.isSameContentAndAuthor(content,author);
        if (exists){
            m.updateTimestamp();
        else{
            int messageId = generateMessageId();
            Message newMessage = new newMessage(messageId,content, timestamp, author,this);
            addMessageToContainer(messageId, newMessage);
        }
        }
    }
    
}
public void publishMessage(int roomId, int userId, String content, int chatId){
    Room r = findRoom(roomId);
    User author = findUser(userId);
    if (r!= null && author != null){
        Chat c = r.findChat(chatId);
        if (c!= null){
            boolean belongsToRoom = r.hasChat(c);
            if (belongsToRoom){
                c.addMessage(content,author);
            }
    }
}

public addMessage(String content, User author){
    
}
}



