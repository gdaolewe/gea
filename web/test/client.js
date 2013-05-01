describe('Djea Tests', function () {
  var expect = require('chai').expect;
  var Browser = require('zombie');
  this.timeout(500000000);
  var b = null;

  it('Should work', function () {
    expect(true).to.be.true;
  });
  it('Should still work', function (done) {
    setTimeout(function () {
      expect(true).to.be.true;
      done();
    }, 500);
  });
  it('Should open the home page with no errors', function (done) {
    Browser.visit('http://localhost:3000', function (e, browser) {
      expect(e).to.be.null;
      expect(browser.errors).to.be.empty;
      b = browser;
      done();
    });
  });
  /*
  //Enter bad search query -> search results should not change
  it('Should not search with just one character', function (done) {
    b.fill('#search-input', 'a', function () {
      b.wait(function (window) {
        //See if the search results
      })
    });
  });*/

  //like/dislike: browser.onalert()
  it('Should not submit a like with no song', function (done) {
    b.fire('div#like', 'click', function () {
     expect(b.prompted('Like submitted! :D')).to.not.be.true;
     done();
    });
  });
});
