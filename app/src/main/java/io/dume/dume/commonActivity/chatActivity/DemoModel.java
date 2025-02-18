package io.dume.dume.commonActivity.chatActivity;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.dume.dume.util.Google;
import io.dume.dume.commonActivity.inboxActivity.InboxCallData;
import io.dume.dume.commonActivity.inboxActivity.InboxNotiData;
import io.dume.dume.teacher.homepage.TeacherContract;
import io.dume.dume.teacher.pojo.Letter;

public class DemoModel {
    private final FirebaseFirestore firestore;
    private final Context context;
    private ListenerRegistration listenerRegistration;
    private static final String TAG = "DemoModel";
    private String opUid, opDP = "", opName, unreadMsgString;
    private Number unreadMsg;
    private Date lastMsgTime;
    boolean mute = false;
    private int flag = 0;

    public DemoModel(Context context) {
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
        unreadMsg = 0;
        unreadMsgString = "";
        lastMsgTime = Calendar.getInstance().getTime();
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
                        /*I am User*/
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

    public void onInboxChange(int identifier, TeacherContract.Model.Listener<Letter> messageListener) {
        firestore.collection("messages").document(Google.getInstance().getCurrentRoom()).collection("chatbox").orderBy("timestamp", Query.Direction.DESCENDING).limit(1).addSnapshotListener((Activity) context, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> documents = null;
                if (e != null) {
                    messageListener.onError(e.getCode() + e.getMessage());
                    return;
                }
                if (queryDocumentSnapshots != null) {
                    documents = queryDocumentSnapshots.getDocuments();
                }
                Letter letter = null;
                if (documents != null && documents.size() > 0) {
                    letter = documents.get(0).toObject(Letter.class);
                    if (letter != null) {
                        letter.setIdentifier(identifier);
                        messageListener.onSuccess(letter);
                    } else messageListener.onError("null found");
                } else {
                    messageListener.onError("null found");
                }
            }
        });
    }

    public void readLastThirty(DocumentSnapshot from, TeacherContract.Model.Listener<List<Letter>> messageListener) {
        Query query = firestore.collection("messages").document(Google.getInstance().getCurrentRoom()).collection("chatbox").orderBy("timestamp", Query.Direction.DESCENDING);
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
                    if (letter != null) {
                        letter.setDoc(documents.get(i));
                    }
                    letters.add(letter);
                }
                messageListener.onSuccess(letters);
                if (e != null) {
                    messageListener.onError(e.getCode() + e.getMessage());
                }
            }
        });
    }

    public void readLastThirtyOnce(TeacherContract.Model.Listener<List<Letter>> messageListener) {
        Query query = firestore.collection("messages").document(Google.getInstance().getCurrentRoom()).collection("chatbox").orderBy("timestamp", Query.Direction.DESCENDING);
        Source source = Source.SERVER;
        query.limit(30).get(source).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Letter> letters = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d(TAG, document.getId() + " => " + document.getData());
                        Letter letter = document.toObject(Letter.class);
                        if (letter != null) {
                            letter.setDoc(document);
                        }
                        letters.add(letter);
                    }
                    messageListener.onSuccess(letters);
                } else {
                    messageListener.onError(task.getException().getLocalizedMessage());
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public String opponentUid(List<String> participants) {
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
                                    inboxNotiData.setTimestapm(documentSnapshot.getDate("date"));
                                    list.add(inboxNotiData);
                                }
                            }
                            listener.onSuccess(list);
                        } else listener.onSuccess(list);
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
                if (e != null || queryDocumentSnapshots == null) {
                    listener.onError(e != null ? e.getMessage() : "No Message History Found. Click Message Button To Create New One");
                } else {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    List<Room> roomList = new ArrayList<>();
                    for (int i = 0; i < documents.size(); i++) {
                        DocumentSnapshot snapshot = documents.get(i);
                        Map<String, Object> map = snapshot.getData();
                        if (map != null) {
                            List<String> participants = (List<String>) map.get("participants");
                            opUid = opponentUid(participants);
                            Map<String, Object> opMap = (Map<String, Object>) map.get(opUid);
                            opName = (String) opMap.get("name");
                            opDP = (String) opMap.get("dp");
                            mute = (Boolean) opMap.get("mute");
                            unreadMsg = (Number) opMap.get("unread_msg");
                            unreadMsgString = (String) opMap.get("last_msg");
                            lastMsgTime = (Date) opMap.get("last_msg_time");
                            roomList.add(new Room(snapshot.getId(), opUid, opDP, opName, lastMsgTime, mute, unreadMsgString, unreadMsg.intValue()));

                            Google.getInstance().setCurrentRoom(snapshot.getId());
                            onInboxChange(i,new TeacherContract.Model.Listener<Letter>() {
                                @Override
                                public void onSuccess(Letter letter) {
                                    if (letter.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                                        //TYPE = SENDER == myself;
                                        unreadMsg = 1;
                                    } else {
                                        //TYPE = RECIVER == opponent;
                                        unreadMsg = 2;
                                    }
                                    unreadMsgString = letter.getBody();
                                    lastMsgTime = letter.getTimestamp();

                                    for (int j = 0; j < roomList.size(); j++) {
                                        if (roomList.get(j).getRoomId().equals(documents.get(letter.getIdentifier()).getId())) {
                                            Room updatedRoom = new Room(roomList.get(j).getRoomId(), roomList.get(j).getOpponentUid(), roomList.get(j).getOpponentDP(), roomList.get(j).getOpponentName(), lastMsgTime, mute, unreadMsgString, unreadMsg.intValue());
                                            roomList.remove(j);
                                            roomList.add(j, updatedRoom);
                                        }
                                    }
                                    flag++;
                                    //roomList.add(new Room(snapshot.getId(), opUid, opDP, opName, lastMsgTime, mute, unreadMsgString, unreadMsg.intValue()));
                                    if (flag == (documents.size())) {
                                        listener.onSuccess(roomList);
                                    }
                                }

                                @Override
                                public void onError(String msg) {
                                    flag++;
                                    if (flag == (documents.size())) {
                                        if (msg.equals("null found")) {
                                            //roomList.add(new Room(snapshot.getId(), opUid, opDP, opName, lastMsgTime, mute, unreadMsgString, unreadMsg.intValue()));
                                            listener.onSuccess(roomList);
                                        } else {
                                            listener.onError("Network err !!");
                                        }
                                    }
                                }
                            });

                        } else return;
                    }
                    if (documents.size() <= 0) {
                        listener.onSuccess(roomList);
                    }
                }

            });
        } else {
            listener.onError("Session Expired. Please Log In");
        }
    }


    public void getToken(String uid, TeacherContract.Model.Listener<String> listener) {
        firestore.collection("token").document(uid).addSnapshotListener((Activity) context, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    listener.onError(e.getLocalizedMessage());
                }
                if (documentSnapshot != null) {
                    String token = documentSnapshot.getString("token");
                    listener.onSuccess(token);
                }
            }
        });

    }


}
