/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

$(function () {
    function mailTo(message) {
        window.location.href = "mailto:" + message.to + "?subject=" + encodeURIComponent(message.subject) + "&body=" + encodeURIComponent(message.body);
    }

    $("#orderNow").on('click', function () {
        var at = '@';
        var dot = '.';
        mailTo({
            "to": "sales" + at + "schlichtherle" + dot + "de",
            "subject": "Subscription to Commercial Licensing Program for TrueLicense",
            "body" : "Hello Sales,\r\n" +
            "\r\n" +
            "I would like to subscribe to the commercial licensing program for TrueLicense. " +
            "Please send me a PayPal invoice.\r\n" +
            "\r\n" +
            "Billing Email: ?\r\n" +
            "Company Name: ?\r\n" +
            "Company Address: ?\r\n" +
            "Product Name: ?\r\n" +
            "Product URL: ?\r\n" +
            "Number of TrueLicense certificates: 1\r\n" +
            "\r\n" +
            "Regards,\r\n" +
            "?"
        });
    });
});
