#GEA
##Introduction

GEA provides a music streaming service with built-in region- based music
trending. This allows users to stream the songs they are interested in and view
the trending songs in particular regions. By rating the songs they listen to,
users contribute to the trending data and increase the popularity of particular
songs in their region. By storing information related to songs played, song
ratings, and location of users, we will be able to display useful trending maps
and provide access to recent trends. Although there are many lists of the top
music hits available, they do not provide region-specific details. Our current
target audience is high school and college aged people as they are the most
likely to both stream music and use location services on mobile devices. Later
in development, the product could be adapted to show worldwide trends, and the
target would obviously shift to accommodate.

##Overview

This project consists of a web server, a browser-based client, and an Android-
based client. The server provides services for the clients such as search and
rating aggregation and so it should be running in order for the clients to
function. The server is also responsible for serving the browser-based client.
For instructions on how to set up the server, see
[`web/README.md`](web/README.md). For instructions on how to build the Android
client, see [`android/README.md`](android/README.md). There is also a pre-
compiled `apk` available for download to use on Android
[here](https://s3.amazonaws.com/OpenGea/Gea.apk).

##Issues and Milestones
Issues are tracked [here on Github](https://github.com/OpenGea/gea/issues).

You must have a Github account in order to submit issues. To create a GitHub
account, go to <https://github.com/> and follow the instructions to sign up for
a free account.

1. Go to <https://github.com/OpenGea/gea>.
2. On the line of buttons under **"OpenGea/gea"**, click on the `Issues` button. From here, a list of open issues is available and more details can be seen for each issue by clicking on the title.
3. Below the `Issues` button, click on the `New Issue` button. `New Issue` will turn blue when the cursor is over the button. If you are not signed in, GitHub will prompt for your login information.
4. In the **Title** field write up an appropriate and descriptive title for the issue, and in the **Write** field include a detailed description of the issue, bug, or feature request.
5. Once an appropriate title and description has been written, click the `Submit new issue` button at the bottom of the text form.
6. Once the issue has been submitted, a new issue will be created and displayed as **"Open"**. The GitHub account used to create the issue is displayed as the creator and allowed to edit the issue at a later date by clicking the `Edit` button.

##LICENSE

**The MIT License**

Copyright (c) 2013 Chaniel Chadowitz, Kimberly Cho, George Daole-Wellman,
Jonathan Godin, Nicholas Hirakawa, Benjamin Holland, Christine King, and Kenneth
Powers.

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
