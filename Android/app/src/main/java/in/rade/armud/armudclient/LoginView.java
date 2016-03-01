package in.rade.armud.armudclient;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by michael1 on 2/29/16.
 */
public class LoginView extends FrameLayout {

    final EditText EditCharName;
    final Button SubmitButton;

    public LoginView(Context context) {
        super(context);
        View.inflate(context, R.layout.login_layout, this);
        EditCharName = (EditText) findViewById(R.id.editName);
        EditCharName.setGravity(Gravity.CENTER_HORIZONTAL);
        SubmitButton = (Button) findViewById(R.id.enterButton);
    }
}
