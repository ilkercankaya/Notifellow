// app.js
var express = require('express'),
    bodyParser = require('body-parser'),
    path = require('path'),
    fs = require('fs'),
    OneSignal = require('onesignal-node');

    // first we need to create a client
var myClient = new OneSignal.Client({
  userAuthKey: 'ZDQzMzM3ZDgtN2Y3NC00MGFkLWFhZjItZWRmODg2ZTlkMDc3',
  app: { appAuthKey: 'OTI2ZTA3ZDUtYmUzNS00MWJhLWI3N2UtNGViYjE0MGYzNjlh', appId: 'af026588-60a0-4d91-9720-16c9de6e9496' }
});

var app = express();
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(bodyParser.json());
//Create HTTP web server
var server = app.listen(3030, function() {
  var host = server.address().address
  var port = server.address().port

  console.log("App listening at http://%s:%s", host, port)
})
app.use(express.static('/uploads'));


//Note that returned querys have ID and friends column but friends contain an json with arrays of friendsID and status
app.get('/', function(req, res) {
  res.send('Index page');
})


app.post('/getPnumber', function(req, res) {
  getPnumber(req, res);
})

app.post('/getUniqID', function(req, res) {
  getUniqID(req, res);
})

app.post('/editPnumber', function(req, res) {
  editPnumber(req, res);
})

app.post('/searchDB', function(req, res) {
  searchDB(req, res);
})

app.post('/editName', function(req, res) {
  editName(req, res);
})

app.post('/editUsername', function(req, res) {
  editUsername(req, res);
})

app.post('/addAlarm', function(req, res) {
  addAlarm(req, res);
})

app.post('/getUserFriends', function(req, res) {
  getUserFriends(req, res);
})

app.post('/getUserPendingFriends', function(req, res) {
  getUserPendingFriends(req, res);
})

app.post('/addUser', function(req, res) {
  addUser(req, res);
})

app.post('/deleteUser', function(req, res) {
  deleteUser(req, res);
})

app.post('/deleteAlarm', function(req, res) {
  deleteAlarm(req, res);
})

app.post('/editEmail', function(req, res) {
  editEmail(req, res);
})

app.post('/getFullName', function(req, res) {
  getFullName(req, res);
})

app.post('/getUsername', function(req, res) {
  getUsername(req, res);
})

app.post('/joinEventReq', function(req, res) {
  joinEventReq(req, res);
})

app.post('/joinEventAccept', function(req, res) {
  joinEventAccept(req, res);
})

app.post('/leaveEvent', function(req, res) {
  leaveEvent(req, res);
})

app.post('/getEventReq', function(req, res) {
  getEventReq(req, res);
})

app.post('/getFeed', function(req, res) {
  getFeed(req, res);
})

app.post('/addComment', function(req, res) {
  addComment(req, res);
})

app.post('/deleteComment', function(req, res) {
  deleteComment(req, res);
})

app.post('/getComment', function(req, res) {
  getComment(req, res);
})

app.post('/getGroups', function(req, res) {
  getGroups(req, res);
})

app.post('/getJoinedEvents', function(req, res) {
  getJoinedEvents(req, res);
})
//For 1 - 0
app.post('/addFR', function(req, res) {
  addFR(req, res);
})

//For 2 - 2
app.post('/acceptFR', function(req, res) {
  acceptFR(req, res);
})

//For 2 - 2
app.post('/deleteOrRejectFR', function(req, res) {
  deleteOrRejectFR(req, res);
})

function searchDB(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var emailGiven = req.body.emailGiven;
    var queryGiv = req.body.queryGiv.toLowerCase();
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    var query ={ username : {$regex : queryGiv}};

    dbo.collection("users").findOne({ email: emailGiven}, function(errTwo, resultUser) {
      if (err) {  
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      else{
      var userFriends = resultUser.friends;
      dbo.collection("users").find(query).toArray(function(err, result) {
        if (err){
          console.log("Query Error Occured!");
          res.status(err.status); // or use err.statusCode instead 
          db.close();
          return res.send(err.message);
        }
        else {
          if (result) {   
            console.log(result);
            var foundUsers = [];
            var status = 1;           
            result.forEach(element => { // O (N + V )
              //Get each 
              status = 1
              
              if(Object.keys(userFriends).length == 0 && emailGiven != element.email)
              {
                 foundUsers.push({
                  ppDest: String(element._id),
                  email: element.email,
                  username: element.username,
                  fullName: element.fullName,
                  status: "-1"
                });
              }
              else{                
              userFriends.forEach(elementTwo => {
                if(elementTwo.email == element.email && emailGiven != element.email){//when match is found

                  
                  foundUsers.push({
                    ppDest: String(element._id),
                    email: element.email,
                    username: element.username,
                    fullName: element.fullName,
                    status: String(elementTwo.status)
                  });
                  
                  status = 0;
                 }
              });
              if (status == 1 && emailGiven != element.email) //if we dont have found user as friend
              {
              console.log("else"+status);                
                foundUsers.push({
                  ppDest: String(element._id),
                  email: element.email,
                  username: element.username,
                  fullName: element.fullName,
                  status: "-1"
                });
              }
            }
            });
            db.close();
            console.log(foundUsers);
            return res.send(foundUsers);           
          } else {
            db.close();
            return res.send("ERROR 404"); 
          }
        }
      });
    }
  });
  });
};


function getUniqID(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var emailGiven = req.body.emailGiven;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    var query = { email: emailGiven};

    dbo.collection("users").findOne(query, function(err, result) {
      if (err){
        console.log("Query Error Occured!");
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      else {
        if (result) {
          db.close();    
          console.log(result._id.toHexString());  
          return res.send(result._id.toHexString());
        } else {
          db.close();
          return res.send("ERROR 404"); 
        }
      }
    });
  });
};


function editPnumber(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var pnumberGiven = req.body.pnumberGiven;
    var emailGiven = req.body.emailGiven;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    dbo.collection("users").update( {email: emailGiven}, { $set: { pnumber: pnumberGiven} }, function(err, added) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        console.log("Query Error Occured!");
        return res.send(err.message);
      }
      
      if (added) {
        db.close();  
        console.log("Phone Number Updated!");            
        return res.send("UPDATED 201");
      }
      else{
        db.close();      
        return res.send("FAILED 201");
      }
  });
  });
};

function getPnumber(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var emailGiven = req.body.emailGiven;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    var query = { email: emailGiven};

    dbo.collection("users").findOne(query, function(err, result) {
      if (err){
        console.log("Query Error Occured!");
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      else {
        if (result) {
          db.close();      
          return res.send(result.pnumber);
        } else {
          db.close();
          return res.send("ERROR 404"); 
        }
      }
    });
  });
};


//Delete an alarm with id users friends
function deleteAlarm(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";

  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var emailGiven = req.body.emailGiven;
    var alarmID = req.body.alarmID;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    //Find current usersID and information
    dbo.collection("users").updateMany( {"alarms.orijinalId": alarmID , "alarms.postOwner": emailGiven}, { $pull: { alarms: {postOwner: emailGiven} } }, function(errZer, addedZer) {
      if (errZer){
        res.status(errZer.status); // or use err.statusCode instead 
        db.close();
        return res.send(errZer.message);
      }
      if(!addedZer ) {
        db.close();
        return "Error On Delete";
      }
      else {
    dbo.collection("users").update( {email: emailGiven}, { $pull: { alarms: {id: alarmID} } }, function(err, added) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      if(!added ) {
        db.close();
        return "Error On Delete";
      }
      else {
        console.log(emailGiven+" deleted the alarm with id: "+alarmID);
        //"Following a POST command, this indicates success, but the textual part of the response line indicates the URI by which the newly created document should be known."
        db.close();
        res.send("DELETED 201");
        }
    });
   }
  });
  });
};

function getUsername(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var gEmailGiven = req.body.emailGiven;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    dbo.collection("users").findOne({ email:gEmailGiven}, function(err, result) {
      if (err){
        console.log("Query Error Occured!");
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      else {
        if (result) {
          db.close();      
          return res.send(result.username);
        } else {
          db.close();
          console.log(gEmailGiven);
          return res.send(gEmailGiven); 
        }
      }
    });
  });
};

function getFullName(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var emailGiven = req.body.emailGiven;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    var query = { email: emailGiven};

    dbo.collection("users").findOne(query, function(err, result) {
      if (err){
        console.log("Query Error Occured!");
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      else {
        if (result) {
          db.close();      
          return res.send(result.fullName);
        } else {
          db.close();
          return res.send("ERROR 404"); 
        }
      }
    });
  });
};

//Change users name 
function editName(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var nameGiven = req.body.nameGiven;
    var emailGiven = req.body.emailGiven;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    dbo.collection("users").update( {email: emailGiven}, { $set: { fullName: nameGiven} }, function(err, added) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        console.log("Query Error Occured!");
        return res.send(err.message);
      }
      
      if (added) {
        db.close();  
        console.log("Name Updated!");            
        return res.send("UPDATED 201");
      }
      else{
        db.close();      
        return res.send("FAILED 201");
      }
  });
  });
};

//Change users email 
function editEmail(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var emailGiven = req.body.emailGiven;
    var newEmail = req.body.newEmailGiven;
    var dbo = db.db("notifellow");

    //Skip since firebase makes sure that we dont dublicate on emails

      dbo.collection("users").update( { email : emailGiven }, { $set: { email :newEmail } }, function(err, added) {
        if (err){
          res.status(err.status); // or use err.statusCode instead 
          db.close();
          console.log("Query Error Occured At Second!" + userIDReq + emailGiven);
          return res.send(err.message);
        }
        
        if (added) {
          db.close(); 
          //TODO: CHANGE USERS IMG PAGE
          console.log("UPDATED 201");
          return res.send("UPDATED 201");
        }
        else{
          db.close();      
          console.log("ERROR ");
          return res.send("ERROR");
        }
      });
  });
};

//Change users username 
function editUsername(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var emailGiven = req.body.emailGiven;
    var userIDReq = req.body.userIDGiven.toLowerCase();
    var dbo = db.db("notifellow");

    dbo.collection("users").findOne({ username : userIDReq }, function(err, result) {
      if (err){
        console.log("Query Error Occured At First!");
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      else {
        if (result) {
          db.close();
          console.log(result);
          return res.send("EXISTS"); 
        } 
      }
    });//

      dbo.collection("users").update( { email : emailGiven }, { $set: { username : userIDReq } }, function(err, added) {
        if (err){
          res.status(err.status); // or use err.statusCode instead 
          db.close();
          console.log("Query Error Occured At Second!" + userIDReq + emailGiven);
          return res.send(err.message);
        }
        
        if (added) {
          db.close();      
          console.log("UPDATED 201");
          return res.send("UPDATED 201");
        }
        else{
          db.close();      
          console.log("ERROR ");
          return res.send("ERROR");
        }
      });
  });
};

//Delete an existing user
function deleteUser(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var emailGiven = req.body.emailGiven;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    var query = { email: emailGiven};
    dbo.collection("users").deleteOne(query, function(err, result) {
      if (err){
        console.log("Query Error Occured!");
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      else {
        if (result) {
          //Send the response
              //Find current adedID and information
          dbo.collection("users").updateMany( {"friends.email": emailGiven}, { $pull: { friends: {email: emailGiven} } }, function(err, added) {
            if (err){
              res.status(err.status); // or use err.statusCode instead 
              db.close();
              return res.send(err.message);
            }
            if(!added ) {
              console.log(emailGiven+" didnt delete the friend request.");
              db.close();
              return "Error on Delete";
            }
            else {
             dbo.collection("users").updateMany( {"alarms.comments.email": emailGiven}, { $pull: { "alarms.comments": {email: emailGiven} } }, function(errZer, addedZer) {
              if (errZer){
                res.status(errZer.status); // or use err.statusCode instead 
                db.close();
                return res.send(errZer.message);
              }
              if(!addedZer ) {
                console.log(emailGiven+" didnt delete the friend request.");
                db.close();
                return "Error on Delete";
              }
              else {
               dbo.collection("users").updateMany( {"alarms.eventJoiners.email":  emailGiven}, { $pull: { "alarms.eventJoiners": {email: emailGiven} } }, function(errTwo, addedTwo) {
                if (errTwo){
                  res.status(errTwo.status); // or use err.statusCode instead 
                  db.close();
                  return res.send(errTwo.message);
                }
                if(!addedTwo ) {
                  console.log(emailGiven+" didnt delete the friend request.");
                  db.close();
                  return "Error on Delete";
                }
                else {
                  console.log(emailGiven+" had been deleted" );
                  /*CREATED deletedID
                  "Following a POST command, this indicates success, but the textual part of the response line indicates the URI by which the newly created document should be known.
                  */
                 db.close();
                  return res.send("DELETED 201");
                }    
              });

              }    
            });

            }    
          });
        } else {
          db.close();
          return res.send("Empty result had returned!"); 
        }
      }
    });
  });
};

//Add an user
function addUser(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      console.log("err");
      return res.send(err.message);
    }
    console.log("no err");
    
    var usernameGiven = req.body.usernameGiven;
    var emailGiven = req.body.emailGiven;
    var oneSignalID = req.body.oneSignal;
    
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    var query = { username: usernameGiven , email: emailGiven, fullName: "", pnumber :"", oneSignal: oneSignalID , friends: [],  alarms: [] };
    dbo.collection("users").findOne( { username: usernameGiven } , function(err, result) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        console.log("Query Error Occured!");
        return res.send(err.message);
      }
      
      if (result) {
        db.close();      
        return res.send("EXISTS");
      }

      dbo.collection("users").findOne( { email: emailGiven } , function(err, result) {
        if (err){
          res.status(err.status); // or use err.statusCode instead 
          db.close();
          console.log("Query Error Occured!");
          return res.send(err.message);
        }
        
        if (result) {
          db.close();      
          return res.send("EXISTS");
        }

      dbo.collection("users").insertOne(query, function(err, result) {
        if (err){
          res.status(err.status); // or use err.statusCode instead 
          db.close();
          console.log("Query Error Occured!");
          return res.send(err.message);
        }

        if (result) {
          db.close();          
          return res.send("CREATED 201");
        }
        else{
        db.close();        
        return res.send("Failed to insert");
        }
      });
    });
  });
  });
};

//START ASYNC MONGODB CALL FOR GETUSERFRIENDS
var ass = async function(ema, dbo, usersFRs){

  var resultTwo = await dbo.collection("users").findOne({ email : ema  });
  if(resultTwo){
    usersFRs.push({
      email: ema,
      username: resultTwo.username,
      fullName: resultTwo.fullName,
      ppDest:  String(resultTwo._id)
    });
    } 

}

async function getUserFriends(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  let db = await MongoClient.connect(url);

  var id = req.body.emailGiven;
  //Select the database
  var dbo = await db.db("notifellow");
  //run the query
  var query = { email: id };
  
 let result = await dbo.collection("users").findOne(query);

  if (result) {
    console.log(result)
    var usersFRs = [];
    var resultTwo;
    //Send the response
    for(const item of result.friends) {
      if(item.status == "2"){                 
          await ass(item.email, dbo , usersFRs);
      }
    };

    console.log(usersFRs);
    return res.send(usersFRs);
  } else {
    console.log("Empty result had returned!");
    res.send("Empty result had returned!");
  }
  db.close();
};

//END ASYNC CALL FOR MONGODB

//get user pending 1-0
//START ASYNC MONGODB CALL FOR GETUSERFRIENDSPENDING
var assTwo = async function(ema, dbo, usersFRs){
  var resultTwo = await dbo.collection("users").findOne({ email : ema  });
  if(resultTwo){
    usersFRs.push({
      email: ema,
      username: resultTwo.username,
      fullName: resultTwo.fullName,
      ppDest:  String(resultTwo._id)
    });
    } 
}
async function getUserPendingFriends(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  let db = await MongoClient.connect(url);

  var id = req.body.emailGiven;
  //Select the database
  var dbo = await db.db("notifellow");
  //run the query
  var query = { email: id };
  
 let result = await dbo.collection("users").findOne(query);

  if (result) {
    console.log(result)
    var usersFRs = [];
    var resultTwo;
    //Send the response
    for(const item of result.friends) {
      if(item.status == "1"){                 
          await assTwo(item.email, dbo , usersFRs);
      }
    };

    console.log(usersFRs);
    return res.send(usersFRs);
  } else {
    console.log("Empty result had returned!");
    res.send("Empty result had returned!");
  }
  db.close();
};
//END ASYNC CALL FOR MONGODB

//Add users friends
function addFR(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";

  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var userId = req.body.UserID;
    var addedID = req.body.AddedID;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    //Find current usersID and information
    dbo.collection("users").findOneAndUpdate( {email: userId}, { $push: { friends: {email: addedID, status: 0} } }, function(errOne, addedZer) {
      if (errOne){
        res.status(errOne.status); // or use err.statusCode instead 
        db.close();
        return res.send(errOne.message);
      }
      if(!addedZer ) {
        db.close();
        return "Error on Adding";
      }
      else {
            //Find current adedID and information
          dbo.collection("users").findOneAndUpdate( {email: addedID}, { $push: { friends: {email: userId, status: 1} } }, function(err, added) {
            if (err){
              res.status(err.status); // or use err.statusCode instead 
              db.close();
              return res.send(err.message);
            }
            if(!added ) {
              db.close();
              return "Error on Adding";
            }
            else {
              console.log(addedID+" recieved the friend request.");
              /*CREATED 201
              "Following a POST command, this indicates success, but the textual part of the response line indicates the URI by which the newly created document should be known.
              */
             // set target users
                var firstNotification = new OneSignal.Notification({
                  contents: {en: addedZer.value.username + " has added you as a friend!"}
              });
              // set notification parameters
              firstNotification.setParameter('data', {"type": "addFR"});
              firstNotification.setTargetDevices([added.value.oneSignal]);

              // send this notification to All Users except Inactive ones
              myClient.sendNotification(firstNotification, function (err, httpResponse,data) {
                if (err) {
                    console.log('Something went wrong...');
                } else {
                    console.log(data, httpResponse.statusCode + added.value.oneSignal);
                }
              });
              db.close();       
              res.send("CREATED 201");
              }
          });
        console.log(userId+" added the user with id: "+addedID);
        }
    });


  });

  process.on('unhandledRejection', (reason, p) => {
    console.log('Unhandled Rejection at: Promise', p, 'reason:', reason);
    // application specific logging, throwing an error, or other logic here
  });
};

//Reject or Delete users friends
function deleteOrRejectFR(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";

  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var userId = req.body.UserID;
    var deletedID = req.body.deletedID;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    //Find current usersID and information
    dbo.collection("users").update( {email: userId}, { $pull: { friends: {email: deletedID} } }, function(err, added) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      if(!added ) {
        db.close();
        return "Error On Delete";
      }
      else {
        console.log(userId+" deleted the user with id: "+deletedID);
        }
    });

    //Find current adedID and information
    dbo.collection("users").update( {email: deletedID}, { $pull: { friends: {email: userId} } }, function(err, added) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      if(!added ) {
        console.log(deletedID+" didnt delete the friend request.");
        db.close();
        return "Error on Delete";
      }
      else {
        console.log(deletedID+" deleted the user with id: "+userId);
        /*CREATED deletedID
        "Following a POST command, this indicates success, but the textual part of the response line indicates the URI by which the newly created document should be known.
        */
       db.close();
        return res.send("DELETED 201");
         }    
    });
  });
  process.on('unhandledRejection', (reason, p) => {
    console.log('Unhandled Rejection at: Promise', p, 'reason:', reason);
    // application specific logging, throwing an error, or other logic here
  });
};


//Accept friend request
function acceptFR(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";

  var userId = req.body.UserID;
  var addedID = req.body.addedID;
 

  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var dbo = db.db("notifellow");
    dbo.collection("users").findOneAndUpdate( 
    {email: userId, "friends.email" : addedID}, { $set: { "friends.$.status": 2} }
    , function(err, addedZer) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      if(!addedZer ) {
        console.log(userId+" didnt accept the friend request.");
        db.close();
        return "Error on Accept";
      }
      else {
        MongoClient.connect(url, function(err, db) {
          if (err) {  
            res.status(err.status); // or use err.statusCode instead 
            db.close();
            return res.send(err.message);
          }
          var dbo = db.db("notifellow");
          dbo.collection("users").findOneAndUpdate(
          {email: addedID, "friends.email" : userId}, { $set: { "friends.$.status": 2} }
          , function(err, added) {
            if (err){
              res.status(err.status); // or use err.statusCode instead 
              db.close();
              return res.send(err.message);
            }
            if(!added ) {
              console.log(addedID+" didnt accept the friend request.");
              db.close();
              return res.send("Error");
            }
            else {
              console.log(addedID+" accepted the user with id: "+userId);

               // set target users
               var firstNotification = new OneSignal.Notification({
                contents: {en: addedZer.value.username + " has accepted your friend request!"}
                });
                // set notification parameters
                firstNotification.setParameter('data', {"type": "acceptFR"});
                firstNotification.setTargetDevices([added.value.oneSignal]);

                // send this notification to All Users except Inactive ones
                myClient.sendNotification(firstNotification, function (err, httpResponse,data) {
                  if (err) {
                      console.log('Something went wrong...');
                  } else {
                      console.log(data, httpResponse.statusCode + added.value.oneSignal);
                  }
                });

              db.close();              
              res.send("UPDATED 201");
              }
          });
        
         });

        console.log(userId+" accepted the user with id: "+addedID);
        }    
      });
      db.close();
    });
  process.on('unhandledRejection', (reason, p) => {
    console.log('Unhandled Rejection at: Promise', p, 'reason:', reason);
    // application specific logging, throwing an error, or other logic here
  });
};
/* Algorihtm of FR:
0 -> Hasnt added yet. (The other user had sent a request though.)
1 -> Added but the user on the other side needs to accept as well.
2 -> They are friends now.
So 0 - 1 => One of them added the other one needs to either accept or reject.
1 - 1 => Becomes 2 - 2 Means they both added each other.
0 - 0 => Deleted from the JSON means one them has rejected another. Deleted to have a faster iteration in JSON parsing.
*/

//Add an alarm existing user
function addAlarm(req,res){
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var userId = req.body.UserID;
    var givenID = req.body.id;
    var givenTitle = req.body.title;
    var givenLocation = req.body.location;
    var givenStartDate = req.body.startDate;
    var givenEndDate = req.body.endDate;
    var public = req.body.public;
    var remindAtGV = req.body.remindAt;
    var initComment = req.body.initialComment;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query

    dbo.collection("users").findOne( { email: userId } , function(errZer, resultZer) {
      if (errZer){
        res.status(errZer.status); // or use err.statusCode instead 
        db.close();
        console.log("Query Error Occured!");
        return res.send(errZer.message);
      }
      
      if (resultZer) {
    var comm = [];
    if(initComment != "")
    comm.push({email: userId, comment: initComment, username: resultZer.username});

    var query = { id: givenID,
		title: givenTitle,
		location: givenLocation,
		startTime : givenStartDate,
    endTime : givenEndDate,
    remindAt: remindAtGV,
    privacy : public,
    userORJoinedAlarm : "1",
    eventJoiners: [],
    comments: comm };
    dbo.collection("users").update( {email: userId}, { $push: { alarms : query } }, function(err, added) {
      if (err){
        console.log("Connection Fail!");
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      else {
        if (added) {
          //Send the response
          db.close();
          return res.send("CREATED 201");
        } else {
          db.close();
          return res.send("QUERY ADD FAIL"); 
        }
      }
    });
  
  }
  });
  });
};

function joinEventReq(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";

  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }

    var userId = req.body.UserID;
    var addedID = req.body.AddedID;
    var eventIDGiv = req.body.eventID;
    var eventname = req.body.eventName;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    //Find current usersID and information
    dbo.collection("users").findOne( { email: userId } , function(errZer, result) {
            //Find current adedID and information
            if (errZer) {
              return console.log("error: " + errZer);
            }
            if(result)
            console.log("ASDSADUASDUASDUAUD");

          dbo.collection("users").findOneAndUpdate( {email: addedID, "alarms.id": eventIDGiv},{ $push: 
          {"alarms.$.eventJoiners": 
              {
                email: userId, 
                status: "1",
                username: result.username
              }
          }
          } , function(err, added) {
            if (err){
              console.log("err")
              res.status(err.status); // or use err.statusCode instead 
              db.close();
              return res.send(err.message);
            }
            if(!added ) {
              db.close();
              onsole.log("err on add")
              return "Error on Adding";
            }
            else {
              console.log(addedID+" recieved the join event request.");
              /*CREATED 201
              "Following a POST command, this indicates success, but the textual part of the response line indicates the URI by which the newly created document should be known.
              */
             // set target users
             console.log(added);
             console.log(result);
                var firstNotification = new OneSignal.Notification({
                  contents: {en: result.username + " has requested to join your event " + eventname}
              });
              // set notification parameters
              firstNotification.setParameter('data', {"type": "joinEventReq"});
              firstNotification.setTargetDevices([added.value.oneSignal]);

              // send this notification to All Users except Inactive ones
              myClient.sendNotification(firstNotification, function (err, httpResponse,data) {
                if (err) {
                    console.log('Something went wrong...');
                } else {
                    console.log(data, httpResponse.statusCode + added.value.oneSignal);
                }
              });
              db.close();       
              res.send("CREATED 201");
              }
          });
        console.log(userId+" had requested to join event for: " + eventIDGiv + " " + addedID);
      });
  });

  process.on('unhandledRejection', (reason, p) => {
    console.log('Unhandled Rejection at: Promise', p, 'reason:', reason);
    // application specific logging, throwing an error, or other logic here
  });
};


//BEGIN OF ASYNC GETFEED

var assThree =  async function(element, dbo, feed, id){

  let oneFriend = await dbo.collection("users").findOne({email: element.email});
  for(const elementTwo of oneFriend.alarms) {
         if (elementTwo.privacy == "1")
         {
           var NotFound = true;
           for (i = 0; i < elementTwo.eventJoiners.length && NotFound; i++) { 
            if(elementTwo.eventJoiners[i].email == id && (elementTwo.eventJoiners[i].status == "2" ||elementTwo.eventJoiners[i].status == "1") ){
              feed.push({
                "email": oneFriend.email,
                "username": oneFriend.username,
                "id" :  elementTwo.id,
                "title" :  elementTwo.title,
                "location" :  elementTwo.location,
                "startTime" :  elementTwo.startTime,
                "endTime" :  elementTwo.endTime,
                "remindAt" :  elementTwo.remindAt ,
                "eventJoiners": elementTwo.eventJoiners,
                "joinedForCurrUser": "1",
                "comments" : elementTwo.comments
               });
              NotFound = false;
            }
          }
          if(NotFound){
          feed.push({
          "email": oneFriend.email,
          "username": oneFriend.username,
          "id" :  elementTwo.id,
          "title" :  elementTwo.title,
          "location" :  elementTwo.location,
          "startTime" :  elementTwo.startTime,
          "endTime" :  elementTwo.endTime,
          "remindAt" :  elementTwo.remindAt ,
          "eventJoiners": elementTwo.eventJoiners,
          "joinedForCurrUser": "0",
          "comments" : elementTwo.comments
         });
        }
        }
       };
}

async function getFeed (req, res)
{ 
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  let db = await MongoClient.connect(url);

  var id = req.body.emailGiven;
  //Select the database
  var dbo = await db.db("notifellow");
  //run the query
  var query = { email: id };
  
 let userFriend = await dbo.collection("users").findOne(query);

 var feed = [ ];

 if (userFriend) {
  for(const element of userFriend.friends) {
    if(element.status == "2")
    {
     await assThree(element, dbo, feed, id);   
    } 
  };
  console.log(feed);
  return res.send(feed);
 }

 else{
  console.log("Empty result had returned in first query!");
  res.send("ERROR 404");
 }
}

//END OF ASYNC GETFEED

function addComment(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      console.log("err");
      return res.send(err.message);
    }
    console.log("no err");
    
    var commentedEmail = req.body.commentedEmail;
    var postOwner = req.body.postOwner;
    var alarmID = req.body.alarmID;
    var comment = req.body.comment;
    var time = req.body.givenTime;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    dbo.collection("users").findOne( { email: commentedEmail } , function(errZer, resultZer) {
      if (errZer){
        res.status(errZer.status); // or use err.statusCode instead 
        db.close();
        console.log("Query Error Occured!");
        return res.send(errZer.message);
      }
      
      if (resultZer) {
        
        dbo.collection("users").findOneAndUpdate( {email: postOwner , "alarms.id": alarmID}, { $push: { "alarms.$.comments": {email: commentedEmail, username: resultZer.username, comment: comment, ppDest: String(resultZer._id) , timeCommented: time} } }, function(err, added) {
          if (err){
            res.status(err.status); // or use err.statusCode instead 
            db.close();
            return res.send(err.message);
          }
          if(!added ) {
            db.close();
            return "Error on Adding";
          }
          else {
            console.log(postOwner+" recieved the comment.");
            /*CREATED 201
            "Following a POST command, this indicates success, but the textual part of the response line indicates the URI by which the newly created document should be known.
            */
           // set target users
              var firstNotification = new OneSignal.Notification({
                contents: {en: resultZer.username + " has commented on your post!"}
            });
            // set notification parameters
            firstNotification.setParameter('data', {"type": "addComment"});
            firstNotification.setTargetDevices([added.value.oneSignal]);

            // send this notification to All Users except Inactive ones
            myClient.sendNotification(firstNotification, function (err, httpResponse,data) {
              if (err) {
                  console.log('Something went wrong...');
              } else {
                  console.log(data, httpResponse.statusCode + added.value.oneSignal);
              }
            });
            db.close();       
            res.send("CREATED 201");
            }
        });

      }

  });
  });
}

//Reject or Delete users friends
function deleteComment(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";

  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var postOwner = req.body.postOwner;
    var deleteEmail = req.body.deleteEmail;
    var alarmID = req.body.alarmID;

    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    //Find current usersID and information
    dbo.collection("users").update( {email: postOwner, "alarms.id": alarmID}, { $pull: { "alarms.$.comments": {email: deleteEmail} } }, function(err, added) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      if(!added ) {
        db.close();
        return "Error On Delete";
      }
      else {
        console.log(deleteEmail+" deleted the comment from "+postOwner);
        db.close();
        return res.send("DELETED 201");  
      }
    });

  });
  process.on('unhandledRejection', (reason, p) => {
    console.log('Unhandled Rejection at: Promise', p, 'reason:', reason);
    // application specific logging, throwing an error, or other logic here
  });
};

function getComment(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      console.log("err");
      return res.send(err.message);
    }
    
    var postOwner = req.body.postOwner;
    var taskID = req.body.taskID;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    dbo.collection("users").findOne( { email: postOwner } , function(errZer, resultZer) {
      if (errZer){
        res.status(errZer.status); // or use err.statusCode instead 
        db.close();
        console.log("Query Error Occured!");
        return res.send(errZer.message);
      }
      
      if (resultZer) {
        resultZer.alarms.forEach(element => {
          if(element.id == taskID){
            console.log(element.comments);
            return res.send(element.comments);
          }
        });

      }
      else{
        console.log("ERROR 404");
        return res.send("ERROR 404");
      }
  });
  db.close();
});
}

function getGroups(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";
  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      console.log("err");
      return res.send(err.message);
    }
    
    var postOwner = req.body.postOwner;
    var taskID = req.body.taskID;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    dbo.collection("users").findOne( { email: postOwner } , function(errZer, resultZer) {
      if (errZer){
        res.status(errZer.status); // or use err.statusCode instead 
        db.close();
        console.log("Query Error Occured!");
        return res.send(errZer.message);
      }
      
      if (resultZer) {
        resultZer.alarms.forEach(element => {
          if(element.id == taskID){
            var joiners = [ ];
            element.eventJoiners.forEach(elementTwo => {
              if( elementTwo.status == "2" ){
                joiners.push({
                  email: elementTwo.email,
                  username: elementTwo.username
                });
              }
            });
            console.log(joiners);
            return res.send(joiners);
          }
        });

      }
      else{
        console.log("ERROR 404");
        return res.send("ERROR 404");
      }
  });
  db.close();
});
}

//Reject or Delete users friends
function leaveEvent(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";

  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var userId = req.body.UserID;
    var deletedID = req.body.deletedID;
    var eventIDGiv = req.body.eventID;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    //Find current usersID and information

    //Find current adedID and information
    dbo.collection("users").update( {email: deletedID, "alarms.id": eventIDGiv}, { $pull: { "alarms.$.eventJoiners" : {email: userId} } }, function(err, added) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      if(!added ) {
        console.log(deletedID+"FAILED TO leave the event with id: " + eventIDGiv + " from" +userId);
        db.close();
        return "Error on Delete";
      }
      else {
        dbo.collection("users").update( {email: userId, "alarms.orijinalId": eventIDGiv}, { $pull: { alarms: {orijinalId: eventIDGiv} } }, function(err, addedZer) {
        console.log(userId+" left the event with id: " + eventIDGiv + " from" +deletedID);
        /*CREATED deletedID
        "Following a POST command, this indicates success, but the textual part of the response line indicates the URI by which the newly created document should be known.
        */
       if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        return res.send(err.message);
      }
      if(!addedZer ) {
        console.log(deletedID+"FAILED TO leave the event with id: " + eventIDGiv + " from" +userId);
        db.close();
        return "Error on Delete";
      }
      else{
       db.close();
        return res.send("DELETED 201");   
      }
      });
      } 
  });
  });
  process.on('unhandledRejection', (reason, p) => {
    console.log('Unhandled Rejection at: Promise', p, 'reason:', reason);
    // application specific logging, throwing an error, or other logic here
  });
};

//Add users friends
function joinEventAccept(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";

  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var userId = req.body.UserID;
    var addedID = req.body.AddedID;
    var eventIDGiv = req.body.eventID;
    var eventname = req.body.eventName;
    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    //Find current usersID and information
    console.log(addedID);
    dbo.collection("users").findOne( { email: userId } , function(errZer, addedZer) {

          dbo.collection("users").findOneAndUpdate( { email: addedID },
            { $set: { "alarms.$[elem1].eventJoiners.$[elem2].status": "2" } },
            { arrayFilters: [ { "elem1.id": eventIDGiv }, { "elem2.email": userId } ]} 
          , function(err, added) {
            if (err){
              console.log(err);
              res.status(err.status); // or use err.statusCode instead 
              db.close();
              return res.send(err.message);
            }
            if(!added ) {
              db.close();
              return "Error on Adding";
            }
            else {
              console.log(addedID+" has accepted the join event request from: "+ userId);
              /*CREATED 201
              "Following a POST command, this indicates success, but the textual part of the response line indicates the URI by which the newly created document should be known.
              */
             // set target users
             added.value.alarms.forEach(oneAlarm => {
               if(oneAlarm.id == eventIDGiv){
                var query = { 
                  orijinalId: eventIDGiv,
                  postOwner: addedID,
                  postOwnerUsername: added.value.username,
                  title: oneAlarm.title,
                  eventJoiners: [],
                  location: oneAlarm.location,
                  startTime : oneAlarm.startTime,
                  endTime : oneAlarm.endTime,
                  remindAt: oneAlarm.remindAt,
                  userORJoinedAlarm : "0",
                 };
                dbo.collection("users").update( {email: userId}, { $push: { alarms : query } }, function(err, addedLast) {
                  if (err){
                    console.log("Connection Fail!");
                    res.status(err.status); // or use err.statusCode instead 
                    db.close();
                    return res.send(err.message);
                  }
                  else {
                    if (addedLast) {
                      //Send the response
                      var firstNotification = new OneSignal.Notification({
                        contents: {en: added.value.username + " has accepted your join event request of "+ eventname + "!"}
                    });
                    // set notification parameters
                    firstNotification.setParameter('data', {"type": "joinEventAccept"});
                    firstNotification.setTargetDevices([addedZer.oneSignal]);
      
                    // send this notification to All Users except Inactive ones
                    myClient.sendNotification(firstNotification, function (err, httpResponse,data) {
                      if (err) {
                          console.log('Something went wrong...');
                      } else {
                          console.log(data, httpResponse.statusCode + added.value.oneSignal);
                      }
                    });
                    db.close();       
                    res.send("CREATED 201");
  
                    } else {
                      db.close();
                      return res.send("QUERY ADD FAIL"); 
                    }
                  }
                });
               }
             });
            }
        
          });
        console.log(userId+" added the user with id: "+addedID);
    });
  });
}

function getEventReq(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";

  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var userId = req.body.UserID;

    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    //Find current usersID and information
    dbo.collection("users").findOne( { email: userId } , function(err, result) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        console.log("Query Error Occured!");
        return res.send(err.message);
      }
      
      if (result) {
        var joinEventReqs = [];
        result.alarms.forEach(element => {
          element.eventJoiners.forEach(elementTwo => {
            if(elementTwo.status == "1"){
              joinEventReqs.push({
                reqEmail: elementTwo.email,
                title: element.title,
                username: elementTwo.username,
                id: element.id,
                startTime: element.startTime,
                endTime:  element.endTime
              })
            }
          });
        });

        console.log(joinEventReqs);
        db.close();
        return res.send(joinEventReqs);
      }

    });
  });
}

function getJoinedEvents(req, res) {
  var MongoClient = require('mongodb').MongoClient;
  var url = "mongodb://notifellowTeam:notiFELLOW123456*_@localhost:27017/";

  MongoClient.connect(url, function(err, db) {
    if (err) {  
      res.status(err.status); // or use err.statusCode instead 
      db.close();
      return res.send(err.message);
    }
    var userId = req.body.UserID;

    //Select the database
    var dbo = db.db("notifellow");
    //run the query
    //Find current usersID and information
    dbo.collection("users").findOne( { email: userId } , function(err, result) {
      if (err){
        res.status(err.status); // or use err.statusCode instead 
        db.close();
        console.log("Query Error Occured!");
        return res.send(err.message);
      }
      
      if (result) {
        var joinEventReqs = [];
        result.alarms.forEach(element => {
            if(element.userORJoinedAlarm == "0"){
              joinEventReqs.push({
                postOwner: element.postOwner,
                postOwnerUsername: element.postOwnerUsername,
                orijinalId: element.orijinalId,
                title: element.title,
                userORJoinedAlarm: element.userORJoinedAlarm,
                startTime: element.startTime,
                endTime:  element.endTime
              })
            }
        });

        console.log(joinEventReqs);
        db.close();
        return res.send(joinEventReqs);
      }

    });
  });
}

module.exports = app;