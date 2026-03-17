# Review Log

## 2026-03-11

- Screen: Guardian enrollment
- Request: Add student ethnicity checkboxes
- Expected: Chamorro, Carolinian, Micronesian, American, Japanese, Filipino, Korean, Chinese, Other with fill-in blank
- Priority: High
- Status: Done

- Screen: Guardian enrollment
- Request: Add languages spoken
- Expected: Multiple rows with language, proficiency, and rank so the most proficient language is ordered first
- Priority: High
- Status: Done

- Screen: Dashboard Family portal
- request: ability to edit an enrollment. currently it only lists them. 
- Expected: see an edit icon or delete icon for clicking on. If delete icon is selected then enrollment is deleted. if edit icon is selected then load the update page.
- Priority: High
- status: done

- Screen: Guardian enrollment
- Request: that other fill ethnic grayed out unless checkbox selected
- Expected: if other selected, then ethnic other textbox is enable for section. If not checked it is disabled
- Priority: low
- status: done

- Screen: Guardian enrollment
- Request: add empty entry to the drop down of grades so that k4 is not automatically selected.
- Expected: this would mean that  select option has one empty option
- priority: low
- status: done

- Screen: Guardian enrollment
- NOTE: this idea is still be played around with.  Not sure if I am ready to do this yet. Still needs work.
- Request: need validation of what has been completed by parents. 
- Expected: we can add a custom annotation to the entity class saying required. Use point-cut to read each record to see if the field has been appropriate filled out. If not then a percentage would be calculated and displayed on the portal page next to each record (percent compledted) The annotation, will also include standard message to be displayed like "missing yada yada"
- priority: low
- status: done

- Screen: Guardian enrollment
- Request: add student medical information section
- Expected: capture first-pass school medical data including primary physician, clinic, clinic phone, hospital preference, insurance provider, policy number, allergies, chronic conditions, medications, dietary restrictions, activity restrictions, and general medical notes
- Priority: high
- Status: done

- Screen: Guardian enrollment
- Request: add emergency contacts section
- Expected: capture at least two emergency contacts with name, relationship, primary phone, secondary phone, email, and pickup authorization flag
- Priority: high
- Status: done

- Screen: Guardian enrollment
- Request: add medical consent and emergency treatment acknowledgements
- Expected: parent can confirm consent for emergency medical treatment, medication administration guidance, and release-to-emergency-contact permissions
- Priority: medium
- Status: done


- Screen: Finance 
- Request: AFTER PAYMENT is made a pdf receipt should be generated for the person who made the payment or gift.
- Expected: cashier accepts payment, confirms all the details and then applies the payment. a new window will appear with confirmation of the payment. Payment details included on receipt include: date of transaction, who received the transaction (id number only for safety of individual), who made the payment including first, m, last name and id from record in the system. amount payed what the payment was toward and remaining balance if any.
- priority: high
- status: done

- Screen: finance
- Request: that user can click on existing fee type and edit the the default fee amount or description field.
- Expected: user clicks on fees listed below and can edit the fees, and descriptions and default amount. The values will populate right there in the current form with update button instead of add fee type as well as cancel.
- priority: medium
- status: done

- Screen: finance
- Request: Let's break this page into a few smaller ones.  The payment page needs to be uncluttered and seperated from the Enrollment finance clearance section. I would prefer this live on a different page but be visible from a notificaiton on the main dashboard page.
- Expected: Should have option for cashier page, option to Review Finance Clearance page, should have outstanding records page for contacting parents/guardians about payments. The main dashboard page should display these to make it easier to navigate.  Should have a page for fee maintenance.  Additionally, some options here should be limited. For example a cashier shouldnt have override capability or capability to change fee structure. This leads me to believe there might be two types of general finance users: cashier and business admin
- priority: medium
- status: done.