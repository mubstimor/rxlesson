package mubstimor.android.rxlesson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private String greeting = "Hello fron Rx";
    private static final String TAG = MainActivity.class.getSimpleName();
    private Observable<String> animalObservable;
    private DisposableObserver<String> animalObserver, animalObserverAllCaps;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    TextView tvGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvGreeting = (TextView)findViewById(R.id.tvGreeting);

        animalObservable = getAnimalsObservable();

        animalObserver = getAnimalsObserver();
        animalObserverAllCaps = getAnimalObserverAllCaps();

        compositeDisposable.add(
                animalObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String s) throws Exception {
                            return s.toLowerCase().startsWith("b");
                        }
                    })
                    .subscribeWith(animalObserver)
        );

        compositeDisposable.add(
                animalObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String s) throws Exception {
                            return s.toLowerCase().startsWith("c");
                        }
                    })
                    .map(new Function<String, String>() {
                        @Override
                        public String apply(String s) throws Exception {
                            return s.toUpperCase();
                        }
                    })
                    .subscribeWith(animalObserverAllCaps)
        );
    }

    private Observable<String> getAnimalsObservable() {
        return Observable.fromArray(
                "Ant", "Ape",
                "Bat", "Bee", "Bear", "Butterfly",
                "Cat", "Crab", "Cod",
                "Dog", "Dove",
                "Fox", "Frog");
    }

    private DisposableObserver<String> getAnimalsObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.i(TAG, "Name: " + s);
                tvGreeting.setText(s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "All items are emitted!");
            }
        };
    }

    private DisposableObserver<String> getAnimalObserverAllCaps() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.d(TAG, "Name: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
