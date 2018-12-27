package vanshika.stickers.com.bobblenotification;

import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CustomSnackbar extends BaseTransientBottomBar<CustomSnackbar> {

    protected CustomSnackbar(@NonNull ViewGroup parent, @NonNull View content, @NonNull ContentViewCallback contentViewCallback) {
        super(parent, content, contentViewCallback);
    }
    public static CustomSnackbar make(ViewGroup parent, int duration) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_snackbar, parent, false);

        ContentViewCallback callback= new ContentViewCallback(view);
        CustomSnackbar customSnackbar = new CustomSnackbar(parent, view, callback);

        customSnackbar.setDuration(duration);

        return customSnackbar;
    }
    public CustomSnackbar setText(CharSequence text) {
        TextView textView = (TextView) getView().findViewById(R.id.name);
        textView.setText(text);
        return this;
    }

    public CustomSnackbar setAction(CharSequence text, final View.OnClickListener listener) {
        TextView actionView = (TextView) getView().findViewById(R.id.yes_button);
        actionView.setText(text);
        actionView.setVisibility(View.VISIBLE);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                // Now dismiss the Snackbar
                dismiss();
            }
        });
        return this;
    }
    public static class ContentViewCallback
            implements BaseTransientBottomBar.ContentViewCallback {

        private View view;

        public ContentViewCallback(View view) {
            this.view = view;
        }

        @Override
        public void animateContentIn(int delay, int duration) {
            ViewCompat.setScaleY(view, 0f);
            ViewCompat.animate(view).scaleY(1f).setDuration(duration).setStartDelay(delay);

        }

        @Override
        public void animateContentOut(int delay, int duration) {
            ViewCompat.setScaleY(view, 1f);
            ViewCompat.animate(view).scaleY(0f).setDuration(duration).setStartDelay(delay);

        }
    }

}
