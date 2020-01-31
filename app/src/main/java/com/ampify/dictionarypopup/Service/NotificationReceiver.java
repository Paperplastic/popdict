package com.ampify.dictionarypopup.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ampify.dictionarypopup.Constants;
import com.ampify.dictionarypopup.Database.Word;
import com.ampify.dictionarypopup.Searcher.Inquiry;

public class NotificationReceiver extends BroadcastReceiver implements Inquiry.InquiryListener {

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String word = intent.getStringExtra(Constants.WORD_ARG);
        new Inquiry(mContext, NotificationReceiver.this).execute(word);
    }

    @Override
    public void onResponse(Word word) {
        Bundle args = new Bundle();
        args.putString(Constants.WORD_ARG, word.getWord());
        args.putStringArrayList(Constants.NOUN_ARG, word.getNoun());
        args.putStringArrayList(Constants.VERB_ARG, word.getVerb());
        args.putStringArrayList(Constants.ADJ_ARG, word.getAdj());
        args.putStringArrayList(Constants.ADV_ARG, word.getAdv());
        args.putBoolean(Constants.FAVOURITE_ARG, word.isSaved());
        args.putString(Constants.NOTE_ARG, word.getNote());
        Intent intent = new Intent(mContext, PopupActivity.class);
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

}
