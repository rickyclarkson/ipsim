package ipsim.swing;

import javax.swing.text.Document;

public interface DocumentValidator {
    boolean isValid(Document document);
}