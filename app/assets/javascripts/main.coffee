root = exports ? this

root.sc_main =
  dataChanged: false
  showChangeContextWarning: () ->
    if(this.dataChanged)
      this.showMessageBar($("#error-bar"),"You haven't saved your changes. If you want to discard them please choose new tab one more time")
      this.dataChanged = false
      false
  showMessageBar : (bar, message) ->
    bar.text(message)
    bar.slideDown(300, ->
      setTimeout ( =>
        $(@).slideUp()
      ), 5000
    )
