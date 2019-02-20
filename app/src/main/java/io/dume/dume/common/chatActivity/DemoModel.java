package io.dume.dume.common.chatActivity;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.dume.dume.Google;
import io.dume.dume.common.contactActivity.ContactActivityModel;
import io.dume.dume.common.contactActivity.ContactData;
import io.dume.dume.common.inboxActivity.InboxCallData;
import io.dume.dume.common.inboxActivity.InboxNotiData;
import io.dume.dume.common.inboxActivity.Notif;
import io.dume.dume.student.recordsPage.RecordsPageModel;
import io.dume.dume.teacher.homepage.TeacherContract;
import io.dume.dume.teacher.homepage.TeacherModel;
import io.dume.dume.teacher.pojo.Letter;

public class DemoModel {
    private final FirebaseFirestore firestore;
    private final Context context;
    private ListenerRegistration listenerRegistration;


    public DemoModel(Context context) {

        this.context = context;
        firestore = FirebaseFirestore.getInstance();
    }


    public void getPhoneNumberList(TeacherContract.Model.Listener<List<InboxCallData>> listener) {

        if (FirebaseAuth.getInstance().getUid() == null) {
            listener.onError("Error Type : Session Expired. Please Login To Feel Better");
            return;
        }

        Query query = firestore.collection("records").whereArrayContains("participants", FirebaseAuth.getInstance().getUid());
        query.addSnapshotListener((Activity) context, (queryDocumentSnapshots, e) -> {
            if (e != null) {
                listener.onError("Error Code : " + e.getCode() + " \n" + e.getLocalizedMessage());

            } else {
                List<DocumentSnapshot> documents = queryDocumentSnapshots != null ? queryDocumentSnapshots.getDocuments() : null;
                if ((documents != null ? documents.size() : 0) > 0) {
                    List<InboxCallData> list = new ArrayList<>();
                    List<String> fooList = new ArrayList<>();
                    for (DocumentSnapshot record : documents) {
                        String gender = "";
                        String name = "";
                        String avatar = "";
                        String phone = "";
                        Map<String, Object> sp_info = (Map<String, Object>) record.get("sp_info");
                        Map<String, Object> sh_info = (Map<String, Object>) record.get("for_whom");


                        String record_status = (String) record.get("record_status");
                        String sh_uid = record.getString("sh_uid");
                        List<String> pList = (List<String>) record.get("participants");
                        int participant;
                        String opponent_uid;
                        if (pList != null) {
                            participant = pList.indexOf(FirebaseAuth.getInstance().getUid());
                        } else {
                            listener.onError("Participant Not Found");
                            return;
                        }
                        if (participant == 0) {
                            opponent_uid = pList.get(1);
                        } else {
                            opponent_uid = pList.get(0);
                        }
                        /*I am Student*/
                        if (sh_uid.endsWith(FirebaseAuth.getInstance().getUid())) {
                            if (sp_info != null) {
                                gender = (String) sp_info.get("gender");
                                avatar = (String) sp_info.get("avatar");
                                name = sp_info.get("first_name") + " " + sp_info.get("last_name");
                                phone = (String) sp_info.get("phone_number");
                            }
                        }/*I am Teacher*/ else {
                            if (sh_info != null) {
                                gender = (String) sh_info.get("request_gender");
                                String stu_photo = (String) sh_info.get("stu_photo");
                                if (stu_photo != null && !sh_info.get("stu_photo").equals("")) {
                                    avatar = stu_photo;
                                } else avatar = (String) sh_info.get("request_avatar");
                                name = (String) sh_info.get("stu_name");
                                phone = (String) sh_info.get("stu_phone_number");

                            }
                        }


                        InboxCallData inboxCallData = new InboxCallData(name, avatar, phone, opponent_uid);

                        if (record_status != null && (record_status.equals("Accepted") || record_status.equals("Current"))) {
                            if (fooList.contains(opponent_uid)) {

                            } else {
                                fooList.add(opponent_uid);
                                list.add(inboxCallData);
                            }
                        }


                    }

                    if (list.size() > 0) {
                        listener.onSuccess(list);

                    } else listener.onError("No Phone Records");
                } else {
                    listener.onError("You don't have any records right now. \nRecord is a deal between Mentor and Students");
                }


            }
        });
    }


    public void addMessage(String roomId, Letter letter, TeacherContract.Model.Listener<Void> listener) {

        firestore.collection("messages").document(roomId).collection("chatbox").add(letter).addOnSuccessListener((Activity) context, new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                listener.onSuccess(null);
            }
        });

    }


    public void addRoom(Map<String, Object> map, TeacherContract.Model.Listener<Void> listener) {
        List<String> participants = new ArrayList<>((List<String>) map.get("participants"));
        Collections.sort(participants);
        DocumentReference messages = firestore.collection("messages").document(participants.get(0).concat(participants.get(1)));
        messages.set(map, SetOptions.merge());
        messages.addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    listener.onError(e.getMessage());
                } else {
                    listener.onSuccess(null);
                }
            }
        });

    }

    public void onTypeStateChange(TeacherContract.Model.Listener<Boolean> listener) {
        firestore.collection("messages").document(Google.getInstance().getCurrentRoom()).addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {

            private boolean isTypeing = false;

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                Map<String, Object> data = (Map<String, Object>) documentSnapshot.get("typing");
                List<String> participants = (List<String>) documentSnapshot.get("participants");
                if (data != null && participants != null) {
                    String opponentUid = opponentUid(participants);
                    Log.w("foo", "onEvent: " + isTypeing);

                    isTypeing = (boolean) data.get(opponentUid);
                }
                listener.onSuccess(isTypeing);
            }
        });
    }

    public void onType(boolean typing, TeacherContract.Model.Listener<Void> listener) {
        firestore.collection("messages").document(Google.getInstance().getCurrentRoom()).update("typing." + FirebaseAuth.getInstance().getUid(), typing).addOnSuccessListener((Activity) context, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listener.onSuccess(aVoid);
            }
        });
    }

    public void onInboxChange(/*String room_id,*/ TeacherContract.Model.Listener<List<Letter>> messageListener) {
        firestore.collection("messages").document(Google.getInstance().getCurrentRoom()).collection("chatbox").orderBy("timestamp", Query.Direction.DESCENDING).limit(1).addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                List<Letter> letters = new ArrayList<>();
                for (int i = 0; i < documents.size(); i++) {
                    Letter letter = documents.get(i).toObject(Letter.class);
                    letters.add(letter);
                }
                messageListener.onSuccess(letters);
                if (e != null) {
                    messageListener.onError(e.getCode() + e.getMessage());
                }
            }
        });
    }

    public void readLastThirty(String from, TeacherContract.Model.Listener<List<Letter>> messageListener) {
        Query query = firestore.collection("messages").document(Google.getInstance().getCurrentRoom()).collection("chatbox").orderBy("timestamp", Query.Direction.ASCENDING);
        if (from != null) {
            query = query.startAfter(from);
        }

        listenerRegistration = query.limit(30).addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                listenerRegistration.remove();
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                List<Letter> letters = new ArrayList<>();
                for (int i = 0; i < documents.size(); i++) {
                    Letter letter = documents.get(i).toObject(Letter.class);
                    letters.add(letter);
                }
                messageListener.onSuccess(letters);
                if (e != null) {
                    messageListener.onError(e.getCode() + e.getMessage());
                }
            }
        });
    }

    private String opponentUid(List<String> participants) {
        String foo = participants.get(0);
        String bar = participants.get(1);
        if (foo.equals(FirebaseAuth.getInstance().getUid())) return bar;
        else return foo;
    }

    public void getNotification(String uid, TeacherContract.Model.Listener<List<InboxNotiData>> listener) {
        if (uid != null && !uid.equals("")) {
            Query query = firestore.collection("push_notifications").whereEqualTo("uid", uid).orderBy("date", Query.Direction.DESCENDING);/*.orderBy("timestamp", Query.Direction.ASCENDING);*/
            query.addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    List<DocumentSnapshot> documents = null;
                    List<InboxNotiData> list = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        documents = queryDocumentSnapshots.getDocuments();
                        if (documents.size() > 0) {
                            for (DocumentSnapshot documentSnapshot : documents) {
                                InboxNotiData inboxNotiData = documentSnapshot.toObject(InboxNotiData.class);
                                if (inboxNotiData != null) {
                                    inboxNotiData.setDoc_id(documentSnapshot.getId());
                                    list.add(inboxNotiData);
                                }
                            }
                            listener.onSuccess(list);
                        } else listener.onError("Empty Notification");
                    } else {
                        listener.onError("Unknown Error From Notification" + e.getMessage());
                        Log.w("foo", e.getMessage());

                    }


                }
            });

        } else listener.onError("Error: Session Expired. Please Log In");
    }

    public void getRoom(String uid, TeacherContract.Model.Listener<List<Room>> listener) {
        if (uid != null && !uid.equals("")) {
            Query query = firestore.collection("messages").whereArrayContains("participants", uid);
            query.addSnapshotListener((Activity) context, (queryDocumentSnapshots, e) -> {
                if (e != null || queryDocumentSnapshots == null)
                    listener.onError(e != null ? e.getMessage() : "No Message History Found. Click Message Button To Create New One");
                else {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    List<Room> roomList = new ArrayList<>();
                    for (int i = 0; i < documents.size(); i++) {
                        DocumentSnapshot snapshot = documents.get(i);
                        Map<String, Object> map = snapshot.getData();
                        String opUid, opDP = "", opName, lastMsgTime = "12/3/2019";
                        boolean mute = false;
                        if (map != null) {
                            List<String> participants = (List<String>) map.get("participants");
                            opUid = opponentUid(participants);
                           /* String foo = participants.get(0);
                            String bar = participants.get(1);
                            if (foo.equals(FirebaseAuth.getInstance().getUid())) opUid = bar;
                            else opUid = foo;*/

                            Map<String, Object> opMap = (Map<String, Object>) map.get(opUid);
                            opName = (String) opMap.get("name");
                            Object muteObj = opMap.get("mute");
                            if (muteObj != null) {
                                mute = (boolean) muteObj;
                            }


                        } else return;


                        roomList.add(new Room(snapshot.getId(), opUid, opDP, opName, lastMsgTime, mute))
                        ;


                    }
                    listener.onSuccess(roomList);
                }

            });
        } else {
            listener.onError("Session Expired. Please Log In");
        }
    }


}
