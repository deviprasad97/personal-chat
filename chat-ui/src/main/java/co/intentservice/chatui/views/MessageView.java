package co.intentservice.chatui.views;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.squareup.picasso.Picasso;

import co.intentservice.chatui.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * MessageView is used to help support custom views without defining a custom viewholder,
 * any view that implements this interface is guaranteed to work with the chat-ui.
 * <p>
 * Created by James Lendrem
 */

public abstract class MessageView extends FrameLayout {

    private TextView senderTextView;
    private CircleImageView circleImageView;
    private ImageView imageMessageView;
    private TextView messageTextView;


    /**
     * Method to set the messages text in the view so it can be displayed on the screen.
     *
     * @param message The message that you want to be displayed.
     */
    public void setMessage(String message){
        if(message != null){
            if (messageTextView == null) {
                this.messageTextView = (TextView) findViewById(R.id.message_text_view);
            }
            messageTextView.setVisibility(VISIBLE);
            messageTextView.setText(message);
        }else {
            this.messageTextView = (TextView) findViewById(R.id.message_text_view);
            messageTextView.setVisibility(GONE);
            messageTextView.setText("");
        }
    }

    /**
     * Method to set the messages text in the view so it can be displayed on the screen.
     *
     * @param imageMessage The message that you want to be displayed.
     */
    public void setImageMessage(Uri imageMessage){
        if(imageMessage != null){
            if (imageMessageView == null) {
                this.imageMessageView = (ImageView) findViewById(R.id.image_message_view);
                this.messageTextView = (TextView) findViewById(R.id.message_text_view);
            }
            imageMessageView.setVisibility(VISIBLE);
            messageTextView.setVisibility(GONE);
            messageTextView.setText("");
            Picasso.get().load(imageMessage).into(imageMessageView);
        }else{
            this.imageMessageView = (ImageView) findViewById(R.id.image_message_view);
            Picasso.get().cancelRequest(imageMessageView);
            imageMessageView.setImageDrawable(null);
        }

    }

    /**
     * Method to set the timestamp that the message was received or sent on the screen.
     *
     * @param timestamp The timestamp that you want to be displayed.
     */
    public abstract void setTimestamp(String timestamp);

    /**
     * Method to set the background color that you want to use in your message.
     *
     * @param background The background that you want to be displayed.
     */
    public abstract void setBackground(int background);

    /**
     * Method to set the elevation of the view.
     *
     * @param elevation The elevation that you want the view to be displayed at.
     */
    public abstract void setElevation(float elevation);

    /**
     * Method to set sender's profile icon.
     *
     * @param uri The url of the pic that you want the view to be display.
     */
    public void setProfileIcon(String uri){
        if(circleImageView == null){
            this.circleImageView = (CircleImageView)findViewById(R.id.profile_icon);
        }
        circleImageView.setVisibility(VISIBLE);
        Picasso.get().load(Uri.parse(uri)).into(circleImageView);
    }

    /**
     * Method to set the message's sender name.
     *
     * @param sender The name of the sender to be displayed.
     *               The view is only visible if the sender is set
     */
    public void setSender(String sender) {
        if (senderTextView == null) {
            this.senderTextView = (TextView) findViewById(R.id.sender_text_view);
        }

        senderTextView.setVisibility(VISIBLE);
        senderTextView.setText(sender);
    }

    /**
     * Constructs a new message view.
     *
     * @param context
     */
    public MessageView(Context context) {

        super(context);

    }

    /**
     * Constructs a new message view with attributes, this constructor is used when we create a
     * message view using XML.
     *
     * @param context
     * @param attrs
     */
    public MessageView(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

}
