package com.ampify.dictionarypopup.Helper;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

//put files in constants?
public class ExtractWordnetHelper extends AsyncTask<Void, Void, Void> {

    public interface ExtractInterface{
        void extractFinish();
    }

    private Context mContext;
    private ExtractInterface mListener;

    public ExtractWordnetHelper(Context context, ExtractInterface listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        extractWordnet();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mListener.extractFinish();
    }

    public void extractWordnet(){
        File f = unGzip();
        File output = new File(mContext.getExternalFilesDir(null), "Wordnet");
        output.mkdir();
        try {
            unTar(f, output);
        } catch (IOException e) {

        }
    }

    public File unGzip() {
        File inputFile = new File(mContext.getExternalFilesDir(null), "WNdb-3.0.tar.gz");
        final File outputFile = new File(mContext.getExternalFilesDir(null), inputFile.getName().substring(0,
                inputFile.getName().length() - 3));
        try {
            GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
            FileOutputStream out = new FileOutputStream(outputFile);
                IOUtils.copy(in, out);
                in.close();
                out.close();

            return outputFile;
        } catch (IOException e) {

        }
        return outputFile;
    }

    public static List<File> unTar(final File inputFile, final File outputDir)
            throws FileNotFoundException, IOException {
        final List<File> untaredFiles = new LinkedList<File>();
        final InputStream is = new FileInputStream(inputFile);
        TarArchiveInputStream debInputStream = null;
        try {
            debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory()
                    .createArchiveInputStream("tar", is);
            TarArchiveEntry entry = null;
            while ((entry = (TarArchiveEntry)debInputStream.getNextEntry()) != null) {
                final File outputFile = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    if (!outputFile.exists()) {
                        if (!outputFile.mkdirs()) {
                            throw new IllegalStateException(
                                    String.format("Couldn't create directory %s.",
                                            outputFile.getAbsolutePath()));
                        }
                    }
                } else {
                    final File parent = outputFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        if (!parent.mkdirs()) {
                            throw new IOException(
                                    String.format(
                                            "Couldn't create directory %s.",
                                            parent.getAbsolutePath()));
                        }
                    }
                    final OutputStream outputFileStream = new FileOutputStream(outputFile);
                    IOUtils.copy(debInputStream, outputFileStream);
                    outputFileStream.close();
                }
                untaredFiles.add(outputFile);
            }
        } catch (ArchiveException ae) {
            // We rethrow the ArchiveException through a more generic one.
            throw new IOException(ae);
        } finally {
            debInputStream.close();
            is.close();
        }
        return untaredFiles;
    }

}