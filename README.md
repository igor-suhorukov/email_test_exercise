#Project:
It’s known that emails can have attachments, some even have other emails as attachments. This
allows them to have attachments at any level of nesting. Emails can be stored in so called
Internet Message Format, ﬁles in this format usually have .eml extension.

Let’s assume that someone could execute the following operations for arbitrary number of times
in a random order:
- Attach EML or ZIP ﬁle to the email
- Archive list of emails to the root folder of ZIP archive

So the sequence of operations is a storing format for the original set of ﬁles. For example ZIP
archive can contain emails with ZIP archive as attachment and the last one ZIP archives contain
emails with email as attachment (see samples provided).
  
# Requirements:
You need to write a CLI program that extracts ﬁles from the provided ﬁle based on the provided
format. See sample output for the sample above.
You must not guess input ﬁle format and the program should allow the user to specify input
format in some way, as a command line argument for example. Program should raise an error if
an input ﬁle cannot be converted based on the provided format.
For EML ﬁles handling it is highly recommended to use Java Mail library
( https://javaee.github.io/javamail ), MimeMessage, Multipart,BodyPart classes in particular.
For ZIP ﬁles handling it is recommended to use java.util.zip package from Java SE.
Solution should have quite good quality, which means it should be covered by tests, be able to
work with real-life emails (as this one) and quite large ZIP archives.