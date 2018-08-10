# MailSmokeTester
MST enables you to run automated mail smoke tests.


**Usage:**
java -jar mst.jar /path/to/config.json /path/to/testcases.json

Where config.json contains all the necessary mail account information as well as settings:
```
{
  "settings":{
    "sleep_before_get_mails": "20",
    "debug_protocols": false
  },

  "Gmail": {
    "address": "some@gmail.com",
    "login": "some@gmail.com",
    "pw": "*********************",
    "inbox_folder_name": "INBOX",
    "host_imap": "imap.gmail.com",
    . . . 
```

testcases.json contains all the testcases you wish to run. The following example will send an emails from gmail to a local mail server, once clean, once containing a virus (EICAR string) and see if the local mail server will remove the malicious mail.
```
{
  "Test 1 - Virus Check Success":{
    "from"                   :"Gmail",
    "to"                     :"Internal",
    "subject"                :"Some Subject",
    "content"                :"Content!",
    "subject_contain"        :"Some Subject [Virus checked]"
  },
  "Test 2 - Malicious Mail":{
    "from"                   :"Gmail",
    "to"                     :"Internal",
    "subject"                :"Some Malicious Stuff",
    "content"                :"MALICIOUS STUFF! X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*",
    "subject_contain"        :"E-Mail contained malicious content",
    "content_not_contain"    :"MALICIOUS STUFF!"
  },
  "Test 3 - Drop Mail":{
    "from"                   :"Gmail",
    "to"                     :"Internal",
    "subject"                :"Subject [RULE TRIGGER DELETE]",
    "content"                :"The subject will trigger a rule in the recipient inbox to delete the mail",
    "should_not_be_received" :true
  }
}
```

**Output:**
Here the local mail server failed to detect the malicious content (see test 2 failing)
```
Loading Mail Accounts
  |_ Gmail (some@gmail.com)
  |_ Internal (test@internalmail.ch)

Loading Testcases
  |_ Test 1
  |_ Test 2

Clearing Inboxes
  |__ Gmail
  |__ Internal
  |__ Sleeping for 20 s

Receiving Mails
  |__ Gmail received 0 mails
  |__ Internal received 2 mails

Checking Testcases
  |__ ✓ Test 1 passed
  |__ x Test 2 failed due to: Subject does not contain "E-Mail contained malicious content"
  |__ ✓ Test 3 passed

```
