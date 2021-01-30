package com.proton.smartkitchen.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.proton.smartkitchen.Database.MealDatabase;
import com.proton.smartkitchen.Database.Meals;
import com.proton.smartkitchen.R;
import com.proton.smartkitchen.Ui.MainActivity;

import java.util.List;
import java.util.Random;

public class MyWorkerDaily  extends Worker {

    MealDatabase instance;

//    private static final String TAG = "MyWorker";
    public MyWorkerDaily(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        instance = MealDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public Result doWork() {

        Backendless.Data.of(Meals.class).getObjectCount(new AsyncCallback<Integer>() {

            private void createNotificationChannel() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "notify";
                    String description = "notify";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("notify", name, importance);
                    channel.setDescription(description);
                    NotificationManagerCompat manager2 = NotificationManagerCompat.from(getApplicationContext());
                    manager2.createNotificationChannel(channel);
                }
            }

            @Override
            public void handleResponse(Integer response) {

                Random random = new Random();
                int randomMeal = random.nextInt(response);

                boolean isDatabaseEmpty = instance.mealDao().getRandomMeal(randomMeal).isEmpty();
                //Check if Database isn't Empty, Below if statement will be executed if Database isn't Empty.
                if (isDatabaseEmpty == false){
//                    Log.d(TAG, "doWork: enter work Db");
                    List<Meals> randomMeal1 = instance.mealDao().getRandomMeal(randomMeal);

                    createNotificationChannel();

                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    in.putExtra("From", "notifyFrag");
                    in.putExtra("Data",randomMeal1.get(0));
                    PendingIntent pe = PendingIntent.getActivity(getApplicationContext(),0,in,PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "notify")

                            .setDefaults(NotificationCompat.DEFAULT_SOUND)
                            .setPriority(NotificationManagerCompat.IMPORTANCE_MAX)
                            .setLights(Color.parseColor("#2196f3"), 500, 2000)
                            .setSmallIcon(R.drawable.eat)
                            .setContentTitle("Have You Tried "+randomMeal1.get(0).getMealName())
                            .setAutoCancel(true)
                            .setContentIntent(pe);

                    NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

                    manager.notify(randomMeal, notification.build());
                }
                //Below else will be executed if Database is Empty
                else{
                    try {
//                        Log.d(TAG, "doWork: enter work cloud ");
                        DataQueryBuilder builder = DataQueryBuilder.create();
                        builder.setWhereClause("id = '"+randomMeal+"\'");
                        Backendless.Data.of(Meals.class).find(builder,new AsyncCallback<List<Meals>>() {
                            @Override
                            public void handleResponse(List<Meals> response) {

                                createNotificationChannel();

                                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                                in.putExtra("From", "notifyFrag");
                                in.putExtra("Data",response.get(0));
                                PendingIntent pe = PendingIntent.getActivity(getApplicationContext(),0,in,PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "notify")

                                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                                        .setPriority(NotificationManagerCompat.IMPORTANCE_MAX)
                                        .setLights(Color.parseColor("#2196f3"), 500, 2000)
                                        .setSmallIcon(R.drawable.eat)
                                        .setContentTitle("Have You Tried "+response.get(0).getMealName())
                                        .setAutoCancel(true)
                                        .setContentIntent(pe);

                                NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

                                manager.notify(randomMeal, notification.build());

                            }
                            @Override
                            public void handleFault(BackendlessFault fault) {
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
            }
        });
        return Result.success();
    }
}
