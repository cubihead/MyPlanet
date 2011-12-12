package com.beecub.games.planet.dialogs;

import android.app.Dialog;
import android.beecub.games.planet.R;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class CreditDialog extends Dialog {
    private Context context;

    public CreditDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_credits);
        setTitle("Info - Credits");
        setCancelable(true);
        
        TextView tv1 = (TextView) findViewById(R.id.bybeecub);
        tv1.setText(Html.fromHtml("by beecub" + ", " + "<a href=\"" + "http://www.beecub.com\">http://beecub.com/</a>"));
        tv1.setMovementMethod(LinkMovementMethod.getInstance());
        
        TextView tv2 = (TextView) findViewById(R.id.dedicated);
        tv2.setText(context.getResources().getString(R.string.dedicated));
    }
}
