package team.athena.NFC;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class NFCTagTextReader extends AsyncTask<Tag, Void, String> {
    private NdefRecord[] NDEFRecords;
    private int NDEFRecordLanguageCodeLength;
    private String NDEFRecordTextEncoding;

    @Override
    protected String doInBackground(Tag... scannedNFCTagContents) {
        Ndef NFCTagData = Ndef.get(scannedNFCTagContents[0]);
        if (NFCTagDoesNotSupportNDEF(NFCTagData)){
            return null;
        }
        return readTextNDEFRecordsFromTagData(NFCTagData);
    }

    private boolean NFCTagDoesNotSupportNDEF(Ndef nfcTagRecord) {
        return nfcTagRecord != null;
    }


    private String readTextNDEFRecordsFromTagData(Ndef NFCTagData) {
        getNDEFRecordsFromTagData(NFCTagData);
        return readTextRecordsFromNDEFRecords();
    }

    private void getNDEFRecordsFromTagData(Ndef nfcTagRecord) {
        NdefMessage ndefMessage = nfcTagRecord.getCachedNdefMessage();
        NDEFRecords = ndefMessage.getRecords();
    }

    private String readTextRecordsFromNDEFRecords() {
        for (NdefRecord NDEFRecord : NDEFRecords) {
            if(NDEFRecordContainsText(NDEFRecord)){
                try {
                    return readNDEFRecordText(NDEFRecord);
                } catch (UnsupportedEncodingException e) {
                    notifyNFCReadError();
                }
            }
        }
        return null;
    }

    private void notifyNFCReadError() {
        Log.e("NFCTagTextReader", "notifyNFCReadError: Error while reading text from NDEF record.");
    }

    private boolean NDEFRecordContainsText(NdefRecord NDEFRecord) {
        return NDEFRecordUsesWellKnownTNF(NDEFRecord) && NDEFRecordRTDIsText(NDEFRecord);
    }

    private boolean NDEFRecordRTDIsText(NdefRecord NDEFRecord) {
        return Arrays.equals(NDEFRecord.getType(), NdefRecord.RTD_TEXT);
    }

    private boolean NDEFRecordUsesWellKnownTNF(NdefRecord NDEFRecord) {
        return NDEFRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN;
    }

    private String readNDEFRecordText(NdefRecord NDEFRecord) throws UnsupportedEncodingException {
        byte[] NDEFRecordPayload = NDEFRecord.getPayload();
        return decodeNDEFRecordPayloadToString(NDEFRecordPayload);
    }

    private String decodeNDEFRecordPayloadToString(byte[] NDEFRecordPayload) throws UnsupportedEncodingException {
        getTextEncodingFromRecordPayload(NDEFRecordPayload[0]);
        getLanguageCodeLengthFromRecordPayload(NDEFRecordPayload[0]);
        return decodeStringUsingTheSpecifiedCharset(NDEFRecordPayload);
    }

    private void getTextEncodingFromRecordPayload(byte NDEFRecordPayloadFirstByte) {
        NDEFRecordTextEncoding = ((NDEFRecordPayloadFirstByte & 128) == 0) ? "UTF-8" : "UTF-16";
    }

    private void getLanguageCodeLengthFromRecordPayload(byte NDEFRecordPayloadFirstByte) {
        NDEFRecordLanguageCodeLength =  NDEFRecordPayloadFirstByte & 0063;
    }

    private String decodeStringUsingTheSpecifiedCharset(byte[] NDEFRecordPayload) throws UnsupportedEncodingException {
        int languageCodeAndFirstSymbolOffset = NDEFRecordLanguageCodeLength + 1;
        int stringLengthWithoutHeaderSymbols = NDEFRecordPayload.length - languageCodeAndFirstSymbolOffset;
        return new String(NDEFRecordPayload, languageCodeAndFirstSymbolOffset, stringLengthWithoutHeaderSymbols, NDEFRecordTextEncoding);
    }



}