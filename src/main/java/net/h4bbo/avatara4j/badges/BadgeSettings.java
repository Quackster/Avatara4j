package net.h4bbo.avatara4j.badges;

import java.security.*;
import java.util.regex.*;
import javax.xml.*;

public class BadgeSettings {
    private String basePath;
    private boolean isShockwaveBadge;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public boolean isShockwaveBadge() {
        return isShockwaveBadge;
    }

    public void setShockwaveBadge(boolean shockwaveBadge) {
        isShockwaveBadge = shockwaveBadge;
    }
}

/*using System.Security.Cryptography;
using System.Xml.Linq;

namespace Badger
{
    public class GetFromServer
    {

        public static Badge getData(string aData)
        {
            var badge = new Badge(); 

            var _loc1_ = 0;
            string _loc3_ = null;
            string _loc9_ = null;
            string _loc10_ = null;
            var _loc12_ = false;
            var _loc7_ = 4;
            int _loc2_ = 0;
            int _loc8_ = 0;
            int _loc5_ = 0;

            while (_loc1_ < 5)
            {
                var badgePart = new BadgePart();

                _loc1_ = _loc1_ + 1;
            }

            return badge;
        }
    }
}*/
