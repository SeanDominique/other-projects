import * as React from "react";
import Iframe from 'react-iframe';
import firebase from 'firebase/compat/app';
import 'firebase/compat/firestore';
import 'firebase/compat/auth';

// Your Firebase configuration object
var firebaseConfig = {
  apiKey: "AIzaSyALBcaXkwC5f2KtqR3ADgCGALXWq7ibhAI",
  authDomain: "dhf23-ec452.firebaseapp.com",
  projectId: "dhf23-ec452",
  storageBucket: "dhf23-ec452.appspot.com",
  messagingSenderId: "455298772363",
  appId: "1:455298772363:web:4c6c6bd0cd88e49eef138e",
  measurementId: "G-66BLTY8K44"
};

// Initialize Firebase with your configuration
if (!firebase.apps.length) {
  firebase.initializeApp(firebaseConfig);
} else {
  firebase.app(); // if already initialized, use that one
}


function PainRating() {
  const [value, setValue] = React.useState(0); // default pain value

  const handleOnChange = (value) => {
    setValue(value);
  };

  const openTypeform = () => {

    // Anonymous Authentification
    firebase.auth().signInAnonymously()
    .then(() => {
      // Successfully signed in anonymously
      // Add a new document with a generated id.
      firebase.firestore().collection("painRatings").add({
        painRating: value,
        timestamp: firebase.firestore.FieldValue.serverTimestamp()
      })
      .then((docRef) => {
        console.log("Document written with ID: ", docRef.id);
      })
      .catch((error) => {
        console.error("Error adding document: ", error);
      });
    })
    .catch((error) => {
      // Handle Errors here
      var errorCode = error.code;
      var errorMessage = error.message;
      console.error("Error signing in: ", error)
    });


    window.open("https://tjszhcihl4r.typeform.com/to/VVFGDG9s", "_blank");
  };

  return (
    <div> 

      <img src="data:image/jpeg;base64,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" alt="Description of the image" />



      <h2 className="h2-header"> What's your pain like today? </h2>
      <input type='range' min='0' max='10' step='1' value={value} onChange={(e)=>setValue(e.target.value)} />
      <h1> {value} </h1>

      <button onClick={openTypeform}>Log rating!</button>

      {/* <Iframe url="https://tjszhcihl4r.typeform.com/to/VVFGDG9s"
      width="100%"
      height="100%"
      id="myId"
      className="myClassname"
      display="initial"
      position="relative"
      allowFullScreen
      /> */}

    </div>
  );
}

export default PainRating;
