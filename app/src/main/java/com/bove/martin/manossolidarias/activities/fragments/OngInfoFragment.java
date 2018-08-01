package com.bove.martin.manossolidarias.activities.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.LayoutInflaterCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.interfaces.FragmentComunication;
import com.bove.martin.manossolidarias.activities.utils.CircleTransform;
import com.bove.martin.manossolidarias.model.Institucion;
import com.google.gson.Gson;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.iconics.view.IconicsImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class OngInfoFragment extends Fragment {
    private FragmentComunication callback;
    private Institucion ong;

    private ImageView header;
    private ImageView logo;
    private TextView mision;
    private TextView horarioTitulo;
    private TextView horario;
    private TextView miscTitulo;
    private TextView misc;
    private IconicsImageView teleIcon;
    private TextView tele;
    private View dividerTele;
    private IconicsImageView mailIcon;
    private TextView mail;
    private View dividerMail;
    private IconicsImageView webIcon;
    private TextView web;
    private View dividerWeb;
    private IconicsImageView facebookIcon;
    private IconicsImageView twitterIcon;
    private ImageView instagramIcon;
    private IconicsImageView youtubeIcon;

    public SharedPreferences preferences;

    public OngInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (FragmentComunication) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " Debes implementar Fragament Comunication.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_ong_info, container, false);

        getCurrentONG();

        // Instaciamos los elementos
        header = view.findViewById(R.id.infoOngHeader);
        logo = view.findViewById(R.id.infoOngLogo);
        mision = view.findViewById(R.id.infoMision);
        horarioTitulo = view.findViewById(R.id.infoHorarioTittle);
        horario = view.findViewById(R.id.infoHorario);
        miscTitulo = view.findViewById(R.id.infoMiscTittle);
        misc = view.findViewById(R.id.infoMisc);

        teleIcon = view.findViewById(R.id.OngTeleiIcon);
        tele = view.findViewById(R.id.ongInfoTel);
        dividerTele = view.findViewById(R.id.divider1);

        mailIcon = view.findViewById(R.id.OngMailiIcon);
        mail = view.findViewById(R.id.ongInfoEmail);
        dividerMail = view.findViewById(R.id.divider2);

        webIcon = view.findViewById(R.id.OngWebiIcon);
        web = view.findViewById(R.id.ongInfoWeb);
        dividerWeb = view.findViewById(R.id.divider3);

        facebookIcon = view.findViewById(R.id.iconicsImageFB);
        instagramIcon = view.findViewById(R.id.iconicsImageInstagram);
        twitterIcon = view.findViewById(R.id.iconicsImageTwitter);
        youtubeIcon = view.findViewById(R.id.iconicsImageYoutube);

        // Cargamos los elementos
        // TODO hacer un placeholder de manos solidarias para el header o implementar un spinner
        Picasso.with(view.getContext()).load(ong.getHeader_img_url()).placeholder(R.drawable.placeholder).fit().centerCrop().into(header);
        Picasso.with(view.getContext()).load(ong.getLogo_url()).transform(new CircleTransform()).fit().into(logo);
        mision.setText(ong.getDescripcion());

        if(TextUtils.isEmpty(ong.getHorario())) {
            horarioTitulo.setVisibility(View.GONE);
            horario.setVisibility(View.GONE);
        } else {
            horario.setText(ong.getHorario());
        }

        if(TextUtils.isEmpty(ong.getMisc())) {
            miscTitulo.setVisibility(View.GONE);
            misc.setVisibility(View.GONE);
        } else {
            misc.setText(ong.getMisc());
        }

        if(TextUtils.isEmpty(ong.getTelefono())) {
            teleIcon.setVisibility(View.GONE);
            tele.setVisibility(View.GONE);
            dividerTele.setVisibility(View.GONE);
        } else {
            tele.setText(ong.getTelefono());
            tele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeCall(ong.getTelefono());
                }
            });
        }

        if(TextUtils.isEmpty(ong.getEmail())) {
            mailIcon.setVisibility(View.GONE);
            mail.setVisibility(View.GONE);
            dividerMail.setVisibility(View.GONE);
        } else {
            mail.setText(ong.getEmail());
            mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMail(ong.getEmail());
                }
            });
        }

        if(TextUtils.isEmpty(ong.getWeb())) {
            webIcon.setVisibility(View.GONE);
            web.setVisibility(View.GONE);
        } else {
            web.setText(ong.getWeb());
            web.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWeb(ong.getWeb());
                }
            });
        }

        // Social section
        if(TextUtils.isEmpty(ong.getFacebook())) {
           facebookIcon.setVisibility(View.GONE);
        } else {
            facebookIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLink(ong.getFacebook(), "facebook");
                }
            });
        }
        if(TextUtils.isEmpty(ong.getInstagram())) {
            instagramIcon.setVisibility(View.GONE);
        } else  {
            instagramIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLink(ong.getInstagram(), "instagram");
                }
            });
        }
        if(TextUtils.isEmpty(ong.getTwitter())) {
            twitterIcon.setVisibility(View.GONE);
        } else {
            twitterIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLink(ong.getTwitter(), "twitter");
                }
            });
        }
        if(TextUtils.isEmpty(ong.getYoutube())) {
            youtubeIcon.setVisibility(View.GONE);
        } else {
            youtubeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLink(ong.getYoutube(), "youtube");
                }
            });
        }

        return view;
    }

    private void getCurrentONG() {
        if (ong == null ) {
            if (preferences == null) {
                // Obtenemos el objeto guardado previamente en las pref
                preferences = this.getActivity().getSharedPreferences(BaseActivity.SHARED_PREF, Context.MODE_PRIVATE);
            }
            Gson gson = new Gson();
            String json = preferences.getString("institucion", "");
            ong = gson.fromJson(json, Institucion.class);
        }
    }

    private void makeCall(String telefono) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + telefono));
        startActivity(intent);
    }

    private void openMail(String emial) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + emial));
        startActivity(emailIntent);
    }

    private void openWeb(String web) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(web));
        try {
            startActivity(i);
        }catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), getString(R.string.error_no_intent), Toast.LENGTH_SHORT).show();
        }
    }

    // Open links in native apps
    private void openLink(String link, String service) {
        String base_url = "";
        String service_url = "";
        String paquete = "";

        // TODO revisar que se habra bien en facebook
        switch (service) {
            case "facebook": {
                base_url =  "https://m.facebook.com/";
                paquete = "";
                service_url = "";
                break;
            }
            case "twitter": {
                base_url =  "https://twitter.com/";
                paquete = "com.twitter.android";
                service_url = "http://twitter.com/";
                break;
            }
            case "instagram": {
                base_url =  "http://instagram.com/";
                paquete = "com.instagram.android";
                service_url = "http://instagram.com/_u/";
                break;
            }
            case "youtube": {
                base_url =  "https://www.youtube.com/user/";
                paquete = "com.google.android.youtube";
                service_url = "https://www.youtube.com/user/";
                break;
            }
        }

        Uri uri = Uri.parse(service_url + link);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage(paquete);

        try {
            startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            openWeb(base_url + link);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentONG();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
