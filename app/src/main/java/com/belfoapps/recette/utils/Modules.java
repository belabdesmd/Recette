package com.belfoapps.recette.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.belfoapps.recette.R;
import com.belfoapps.recette.base.AppDatabase;
import com.belfoapps.recette.models.GoogleSheetsEndpoints;
import com.belfoapps.recette.models.SharedPreferencesHelper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn({ActivityComponent.class, FragmentComponent.class})
public class Modules {
    public static final String DATABASE_NAME = "9arib";

    public static String getSignature(@NonNull PackageManager pm, @NonNull String packageName) {
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo packageInfo =
                    pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo == null
                    || packageInfo.signatures == null
                    || packageInfo.signatures.length == 0
                    || packageInfo.signatures[0] == null) {
                return null;
            }
            return signatureDigest(packageInfo.signatures[0]);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static String signatureDigest(Signature sig) {
        byte[] signature = sig.toByteArray();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(signature);
            return new BigInteger(1, digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @Provides
    public static Retrofit providesRetrofitInstance(@ApplicationContext Context context) {
        String certs = getSignature(context.getPackageManager(),
                context.getPackageName());
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    assert certs != null;
                    Request request = original.newBuilder()
                            .header("X-Android-Package", context.getPackageName())
                            .header("X-Android-Cert", certs)
                            .build();

                    return chain.proceed(request);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(context.getResources().getString(R.string.BASE_API_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    public static SharedPreferencesHelper providesSharedPreferences(@ApplicationContext Context context) {
        return new SharedPreferencesHelper(context.getSharedPreferences("BASIC", Context.MODE_PRIVATE));
    }

    @Provides
    public static AppDatabase providesAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class, DATABASE_NAME).build();
    }

    @Provides
    public static GoogleSheetsEndpoints providesGoogleSheetsEndpoints(Retrofit retrofit) {
        return retrofit.create(GoogleSheetsEndpoints.class);
    }
}
