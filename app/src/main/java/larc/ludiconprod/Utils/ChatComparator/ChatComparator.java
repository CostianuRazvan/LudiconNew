package larc.ludiconprod.Utils.ChatComparator;

import java.util.Comparator;

import larc.ludiconprod.Utils.Chat;

public class ChatComparator implements Comparator<Chat> {
    @Override
    public int compare(Chat c1, Chat c2) {
        return c2.lastMessageTime.compareTo(c1.lastMessageTime);
    }
}
