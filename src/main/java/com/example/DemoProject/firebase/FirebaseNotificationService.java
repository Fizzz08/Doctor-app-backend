package com.example.DemoProject.firebase;//package com.example.DemoProject.firebase;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FirebaseNotificationService {
//
//    @Autowired
//    private DeviceTokenRepository tokenRepository;
//
//    public String getTokenByPhone(String phoneNumber) {
//        return tokenRepository.findTokenByPhoneNumber(phoneNumber);
//    }
//
//    public void sendPushNotification(String deviceToken) {
//        Message message = Message.builder()
//                .setToken(deviceToken)
//                .setNotification(Notification.builder()
//                        .setTitle("Booking Confirmed")
//                        .setBody("Your appointment has been booked successfully.")
//                        .build())
//                .build();
//
//        try {
//            FirebaseMessaging.getInstance().send(message);
//        } catch (FirebaseMessagingException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
