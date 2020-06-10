// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomQuote() {
    const quotes =
        ['"Sometimes I\'ll start a sentence and I don\'t even know where it\'s going. I just hope I find it along the way." — Michael Scott',
        '"I talk a lot, so I\'ve learned to just tune myself out..." — Kelly Kapoor',
        ' "I am Beyonce always." — Michael Scott',
        '"I am running away from my responsibilities and it feels good." — Michael Scott',
        '"Oh, it is on, like a prawn who yawns at dawn." — Andy Bernard',
        '"Should have burned this place down when I had the chance." — Michael Scott',
        ' "One day Michael came in and complained about a speed bump on the highway. I wonder who he ran over then." — Jim Halpert',
        '"Mini cupcakes? As in the mini version of regular cupcakes? Which is already a mini version of cake? Honestly, where does it end with you people?" — Kevin Malone',
        '"I don\'t care what they say about me. I just want to eat." — Pam Beesly',
        '"I want people to be afraid of how much they love me." — Michael Scott',
        ' "Whenever I\'m about to do something, I think, \'Would an idiot do that?\' and if they would, I do not do that thing." — Dwight Schrute',
        '"I stopped caring a long time ago." — Creed Bratton',
        '"It takes an advanced sense of humor. I don\'t expect everybody to understand." — Michael Scott',
        '"The doctor said, if I can\'t find a new way to relate more positively to my surroundings, I\'m going to die. I\'m going to die." — Stanley Hudson',
        '"I\'m not superstitious, but I am a little stitious." — Michael Scott',
        '"You only live once? False. You live every day. You only die once." — Dwight Schrute',
        '"Bears, beets, Battlestar Galactica" — Jim Halpert',
        ' "I understand nothing." — Michael Scott',
        '"What are your weaknesses?" "I don\'t have any, a--hole." — Kelly Kapoor',
        '"An office is a place where dreams come true." — Michael Scott',
        '"I wish there was a way to know you\'re in the good old days before you\'ve actually left them." — Andy Bernard',
        '"I am one of the few people who looks hot eating a cupcake." — Kelly Kapoor',
        '"If I don\'t have some cake soon, I might die." — Stanley Hudson',
        '"I knew exactly what to do, but in a much more real sense I had no idea what to do." — Michael Scott',
        '"I just wanna lie on the beach and eat hot dogs. That\'s all I\'ve ever wanted." — Kevin Malone',
        '"That\'s what she said." — Michael Scott']

    // Pick a random quote.
    const quote = quotes[Math.floor(Math.random() * quotes.length)];

    // Add it to the page.
    const quoteContainer = document.getElementById('quote-container');
    quoteContainer.innerText = quote;
}

//https://www.w3schools.com/howto/howto_js_tab_img_gallery.asp
function placeInContainer(imgs) {
    var expandImg = document.getElementById("expandedImg");
    var imgText = document.getElementById("imgtext");
    // Use the same src in the expanded image as the image being clicked on from the grid
    expandImg.src = imgs.src;
    imgText.innerHTML = imgs.alt;
    // Show the container element (hidden with CSS)
    expandImg.parentElement.style.display = "block";
}

function getContent() {
    document.getElementById('message').innerHTML = "";
    let max_comments = document.getElementById("max_comments").value;
    let getMessage = (text) => {
        const liElement = document.createElement('li');
        liElement.innerText = text;
        return liElement;
    }

    fetch(`/data?max_comments=${max_comments}`).then(response => response.json()).then((messages) => {
        const container = document.getElementById('message');
        messages.forEach((comment) => {
            container.appendChild(getMessage(comment.email + " : " + comment.message));
        });
    });
}

function deleteComments(){
    // clear out comments stored in Datastore and write to page 
    fetch(new Request("/delete-comments", {method: 'POST'})).then(getContent());
}
