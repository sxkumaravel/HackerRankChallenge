package loginscreen.solution.example.com.loginscreen;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created on 4/21/19.
 *
 * @author kumars
 */
public class SpacesInputFilter implements InputFilter {

    private Context context;

    public SpacesInputFilter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {

            if (Character.isWhitespace(source.charAt(i))) {

                return "";
            }
        }
        return null;
    }
}
