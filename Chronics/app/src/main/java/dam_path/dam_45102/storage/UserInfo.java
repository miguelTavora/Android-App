package dam_path.dam_45102.storage;

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

public class UserInfo {

    public static final String USERS_ACCESS="users";
    public static final String PUBLICATIONS_ACCESS="publications";
    public static final String DATE_ACCESS="date";
    public static final String STORY_ACCESS="history";
    public static final String LIKES_ACCESS="likes";
    public static final String USER_LIKES_ACCESS="user_likes";
    public static final String COMMENTS_ACCESS="comments";
    public static final String BIO_ACCESS="bio";
    public static final String PROFILE_PICTURE_ACCESS="picture";
    public static final String TIMER_ACCESS="timer";
    public static final String GROUPS_ACCESS="groups";
    public static final String DESCRIPTION_GROUPS_ACCESS="description";
    public static final String IMAGE_GROUPS_ACCESS="image";
    public static final String ADMIN_GROUPS_ACCESS="admin";
    public static final String MEMBERS_GROUPS_ACCESS="members";
    public static final String REQUEST_GROUP_ACCESS="request";
    public static final String MESSAGES_GROUP_ACCESS="messages";
    public static final String USER_GROUP_ACCESS="user";
    public static final String MESSAGE_GROUP_ACCESS="message";
    public static final String SOCIAL_POINTS_ACCESS="socialPoints";
    public static final String USER_CONVERSATIONS_ACCESS="conversations";
    public static final String LAYOUT_COLOR_ACCESS="color";//0 -> branco  ||  1 -> preto   ||  2 -> vermelho || 3 -> azul
    public static String username;

    public static final int IMAGE_PICK_CODE = 1000;
    public static final int PERMISSION_CODE = 1001;

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
