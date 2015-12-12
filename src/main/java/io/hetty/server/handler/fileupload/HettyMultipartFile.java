package io.hetty.server.handler.fileupload;

import io.netty.handler.codec.http.multipart.MixedFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yuck on 2015/12/12.
 */
public class HettyMultipartFile implements MultipartFile {
    protected static final Log LOGGER = LogFactory.getLog(HettyMultipartFile.class);

    private final MixedFileUpload fileItem;
    private final long size;

    public HettyMultipartFile(MixedFileUpload fileItem) {
        this.fileItem = fileItem;
        this.size = this.fileItem.length();
    }

    public final MixedFileUpload getFileItem() {
        return fileItem;
    }

    @Override
    public String getName() {
        return this.fileItem.getName();
    }

    @Override
    public String getOriginalFilename() {
        String filename = this.fileItem.getFilename();
        if (filename == null) {
            // Should never happen.
            return "";
        }

        // Check for Unix-style path
        int unixSep = filename.lastIndexOf("/");
        // Check for Windows-style path
        int winSep = filename.lastIndexOf("\\");
        // Cut off at latest possible point
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1) {
            // Any sort of path separator found...
            return filename.substring(pos + 1);
        } else {
            // A plain name
            return filename;
        }
    }

    @Override
    public String getContentType() {
        return this.fileItem.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public byte[] getBytes() throws IOException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        byte[] bytes = this.fileItem.get();
        return (bytes != null ? bytes : new byte[0]);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }

        InputStream inputStream = FileUtils.openInputStream(this.fileItem.getFile());
        return (inputStream != null ? inputStream : StreamUtils.emptyInput());
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        if (!isAvailable()) {
            throw new IllegalStateException("File has already been moved - cannot be transferred again");
        }

        if (dest.exists() && !dest.delete()) {
            throw new IOException(
                    "Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
        }

        try {
            FileUtils.copyFile(this.fileItem.getFile(), dest);
            if (LOGGER.isDebugEnabled()) {
                String action = "transferred";
                if (!this.fileItem.isInMemory()) {
                    action = isAvailable() ? "copied" : "moved";
                }
                LOGGER.debug("Multipart file '" + getName() + "' with original filename [" +
                        getOriginalFilename() + "], stored " + getStorageDescription() + ": " +
                        action + " to [" + dest.getAbsolutePath() + "]");
            }
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("Could not transfer to file", ex);
            throw new IOException("Could not transfer to file: " + ex.getMessage());
        }
    }

    /**
     * Determine whether the multipart content is still available.
     * If a temporary file has been moved, the content is no longer available.
     */
    protected boolean isAvailable() throws IOException {
        // If in memory, it's available.
        if (this.fileItem.isInMemory()) {
            return true;
        }
        // Check actual existence of temporary file.
        return this.fileItem.getFile().exists() && (this.fileItem.length() == this.size);
    }

    /**
     * Return a description for the storage location of the multipart content.
     * Tries to be as specific as possible: mentions the file location in case
     * of a temporary file.
     */
    public String getStorageDescription() throws IOException {
        if (this.fileItem.isInMemory()) {
            return "in memory";
        } else {
            return "at [" + fileItem.getFile().getAbsolutePath() + "]";
        }

    }


}
