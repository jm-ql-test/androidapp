package com.aexp.jmai.qlexam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.aexp.jmai.qlexam.domain.Game;
import com.aexp.jmai.qlexam.domain.GiantBomb;
import com.aexp.jmai.qlexam.service.BackendService;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.aexp.jmai.qlexam.helper.Constants.API_KEY;
import static com.aexp.jmai.qlexam.helper.Constants.FORMAT;
import static com.aexp.jmai.qlexam.helper.Constants.RESOURCES;

public class MainActivity extends AppCompatActivity {
    Subscription subscription;
    ListView     listView;
    EditText     queryEditText;
    Button       searchButton;
    TextView     message;
    ProgressBar  progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        queryEditText = (EditText) findViewById(R.id.query);
        searchButton = (Button) findViewById(R.id.search);
        message = (TextView) findViewById(R.id.emptyElement);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(GONE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressBar.setVisibility(VISIBLE);
                listView.setVisibility(INVISIBLE);
                message.setVisibility(INVISIBLE);
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                //todo probably need to check query validity
                String query = queryEditText.getText().toString();

                subscription = Observable.zip(Observable.just(API_KEY),
                                              Observable.just(query),
                                              Observable.just(FORMAT),
                                              Observable.just(RESOURCES),
                                              new Func4<String, String, String, String, GiantBomb>() {
                                                  @Override public GiantBomb call(final String apiKey, final String query, final String format, final
                                                  String resources) {
                                                      return BackendService.setupQueryClient().getGames(apiKey, query, format, resources);
                                                  }
                                              })
                                         .subscribeOn(Schedulers.io())
                                         .observeOn(AndroidSchedulers.mainThread())
                                         .subscribe(new SearchObserver(MainActivity.this));
            }
        });

    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private static class SearchObserver implements Observer<GiantBomb> {
        final WeakReference<MainActivity> activityWeakReference;

        SearchObserver(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override public void onCompleted() {

        }

        @Override public void onError(final Throwable throwable) {
            Log.e("MainActivity", "error: " + throwable);
            if (activityWeakReference.get() == null) {
                return;
            }
            MainActivity activity = activityWeakReference.get();
            activity.listView.setAdapter(null);
            activity.message.setText(activity.getText(R.string.service_not_available));
            activity.listView.setEmptyView(activity.message);
            activity.progressBar.setVisibility(GONE);
            activity.listView.setVisibility(VISIBLE);
        }

        @Override public void onNext(final GiantBomb giantBomb) {
            if (activityWeakReference.get() == null) {
                return;
            }
            MainActivity activity = activityWeakReference.get();
            Log.d("MainActivity", "number of results: " + giantBomb.number_of_total_results);

            if (!("OK".equals(giantBomb.error))) { //in case server returns not OK.
                onError(new IllegalStateException("Server error."));
            } else {
                List<Game> games = new ArrayList<>();
                for (GiantBomb.Results results : giantBomb.results) {
                    if (results.image != null) {
                        games.add(new Game(results.image.thumb_url, results.name));
                    } else {
                        games.add(new Game(null, results.name));
                    }
                }
                SearchResultAdapter adapter = new SearchResultAdapter(activity, R.layout.list_item, games);
                activity.listView.setAdapter(adapter);
                activity.message.setText(activity.getText(R.string.no_result));
                activity.listView.setEmptyView(activity.message);
                adapter.notifyDataSetChanged();
                activity.progressBar.setVisibility(GONE);
                activity.listView.setVisibility(View.VISIBLE);
            }
        }
    }
}
