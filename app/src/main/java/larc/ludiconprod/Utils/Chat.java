package larc.ludiconprod.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ancuta on 8/18/2017.
 */

public class Chat {
    public String eventId;
    public String chatId;
    public ArrayList<String> image=new ArrayList<>();
    public String participantName;
    public ArrayList<String> otherParticipantId=new ArrayList<>();
    public Double lastMessageTime;
    public String lastMessage;
    public String lastMessageId;
    public String lastMessageSeen="";

    public Date eventDate;
    public String eventSport;
    public String eventLocation;
}
