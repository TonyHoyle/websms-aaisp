package uk.me.hoyle.websms.aaisp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import de.ub0r.android.websms.connector.common.BasicConnector;
import de.ub0r.android.websms.connector.common.ConnectorCommand;
import de.ub0r.android.websms.connector.common.ConnectorSpec;
import de.ub0r.android.websms.connector.common.ConnectorSpec.SubConnectorSpec;
import de.ub0r.android.websms.connector.common.Utils;
import de.ub0r.android.websms.connector.common.WebSMSException;

public final class ConnectorAAISP extends BasicConnector {
	private static final String SMS_URL = "http://sms.aaisp.net.uk/sms.cgi";

	@Override
	protected String getUrlSend(ArrayList<BasicNameValuePair> d) {
		return SMS_URL;
	}

	@Override
	protected String getUrlBalance(ArrayList<BasicNameValuePair> d) {
		return null;
	}

	@Override
	protected String getParamUsername() {
		return "username";
	}

	@Override
	protected String getParamPassword() {
		return "password";
	}

	@Override
	protected String getParamRecipients() {
		return "destination";
	}

	@Override
	protected String getParamText() {
		return "message";
	}

	@Override
	protected String getParamSender() {
		return "origin";
	}
	
	@Override
	protected String getParamFlash() {
		return "flash";
	}
	
	@Override
	protected String getParamSendLater() {
		return "sendtime";
	}

	@Override
	protected String getUsername(Context context, ConnectorCommand command, ConnectorSpec cs) {
		final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		String user = p.getString(Preferences.PREFS_USERNAME, "");
		if(user.length()==0) {
            user = Utils.international2oldformat(Utils.getSender(context, command
                    .getDefSender()));	
		}
		return user;
	}

	@Override
	protected String getPassword(Context context, ConnectorCommand command,
			ConnectorSpec cs) {
		final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		return p.getString(Preferences.PREFS_PASSWORD, "");
	}

	@Override
	protected String getSender(Context context, ConnectorCommand command,
			ConnectorSpec cs) {
		final SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		return p.getString(Preferences.PREFS_ORIGIN, "");
	}

	@Override
	protected String getRecipients(ConnectorCommand command) {
		return Utils.joinRecipientsNumbers(
				Utils.national2international(command.getDefPrefix(),
						command.getRecipients()), ";", true);
	}
	
	@Override
	protected String getSendLater(long sendLater) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(sendLater));
	}
	
	@Override
	protected void addExtraArgs(Context context,
            ConnectorCommand command,
            ConnectorSpec cs,
            java.util.ArrayList<BasicNameValuePair> d) {
        final SharedPreferences p = PreferenceManager
        .getDefaultSharedPreferences(context);
		String report = p.getString(Preferences.PREFS_DELIVERY_REPORT, "");
		if(report.length()>0) {
			d.add(new BasicNameValuePair("report", p.getString(Preferences.PREFS_DELIVERY_REPORT, "")));
		}
	}

	@Override
	protected void parseResponse(Context context, ConnectorCommand command,
			ConnectorSpec cs, String htmlText) {
		if(htmlText.contains("ERR: ")) {
			int index = htmlText.indexOf("ERR: ");
			throw new WebSMSException(htmlText.substring(index));
		}
	}
	
    @Override
    public ConnectorSpec initSpec(final Context context) {
            final String name = context.getString(R.string.app_name);
            ConnectorSpec c = new ConnectorSpec(name);
            c.setAuthor(context.getString(R.string.app_author));
            c.setBalance(null);
            c.setCapabilities(ConnectorSpec.CAPABILITIES_SEND
                            | ConnectorSpec.CAPABILITIES_PREFS);
            c.addSubConnector("uk.me.hoyle.websms.aaisp", "uk.me.hoyle.websms.aaisp", SubConnectorSpec.FEATURE_FLASHSMS | SubConnectorSpec.FEATURE_SENDLATER);
            return c;
    }

    @Override
    public ConnectorSpec updateSpec(final Context context,
                    final ConnectorSpec connectorSpec) {
            final SharedPreferences p = PreferenceManager
                            .getDefaultSharedPreferences(context);
            if (p.getBoolean(Preferences.PREFS_ENABLED, false)) {
                    if (p.getString(Preferences.PREFS_PASSWORD, "").length() > 0) {
                            connectorSpec.setReady();
                    } else {
                            connectorSpec.setStatus(ConnectorSpec.STATUS_ENABLED);
                    }
            } else {
                    connectorSpec.setStatus(ConnectorSpec.STATUS_INACTIVE);
            }
            return connectorSpec;
    }
}
