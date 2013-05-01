describe('Gea Tests', function () {
  var expect = require('chai').expect;
  var Browser = require('zombie');
  this.timeout(500000000);
  var b = null;

  //Test to open the main page with no errors
  it('Should open the home page with no errors', function (done) {
    Browser.visit('http://localhost:3000', function (e, browser) {
      expect(e).to.be.null;
      expect(browser.errors).to.be.empty;
      //Hold onto this browser instance for further testing
      b = browser;
      done();
    });
  });

  //Enter bad search query -> search results should not change
  it('Should not search with just one character', function (done) {
    b.fill('#search-input', 'a').wait(function () {
      //There should be no div's with class 'result' since that means there are no search results
      expect(b.query('.result')).to.be.null;
      done();
    });
  });
  it('Should not search with a single "-"', function (done) {
    b.fill('#search-input', '-').wait(function () {
    //There should be no div's with class 'result' since that means there are no search results
      expect(b.query('.result')).to.be.null;
      done();
    });
  });
  it('Should not search with a leading "-"', function (done) {
    b.fill('#search-input', '-abcdef').wait(function () {
      //There should be no div's with class 'result' since that means there are no search results
      expect(b.query('.result')).to.be.null;
      done();
    });
  });
  //Buttons should not be functional before a song is loaded
  it('Should not submit a like with no song', function (done) {
    //"Click" the like button
    b.fire('div#like', 'click', function () {
      //"Like" alert should not appear
      expect(b.prompted('Like submitted! :D')).to.not.be.true;
      done();
    });
  });
  it('Should not submit a dislike with no song', function (done) {
    //"Click" the dislike button
    b.fire('div#dislike', 'click', function () {
      //"Dislike" alert should not appear
      expect(b.prompted('Dislike submitted! >:(')).to.not.be.true;
      done();
    });
  });
  it('Should not toggle play/pause with no song', function (done) {
    //Should initially have a pause class
    expect(b.query('div.pause-button')).to.not.be.null;
    //"Click" the play/pause button
    b.fire('div#play-pause', 'click', function () {
      //It should not toggle to show the play button (starts as pause)
      expect(b.query('div.play-button')).to.be.null;
      done();
    });
  });
  it('Should not change page when prev is clicked with no song', function (done) {
    //Store the current HTML
    var before = b.html();
    //Click the 'previous' button
    b.fire('div#previous', 'click', function () {
      //The current HTML should be no different from before
      expect(b.html()).to.equal(before);
      done();
    });
  });
  it('Should not change page when next is clicked with no song', function (done) {
    var before = b.html();
    b.fire('div#next', 'click', function () {
      expect(b.html()).to.equal(before);
      done();
    });
  });
  //Map filter drop down should fire an XHR request and change the HTML Select
  it('Should fire a request when the map filter drop down is changed', function (done) {
    var before = b.html();
    b.select('#map-filter', 'Top songs today')
    .wait(function () {
      //'Top songs today' (the first option) should be selected
      expect(b.querySelector('#map-filter').childNodes[1].selected).to.be.true;
      //'Top songs of all time' (the last option) should not be selected
      expect(b.querySelector('#map-filter').childNodes[9].selected).to.be.false;
      done();
    });
  });
});
