require 'facter'

Facter.add(:gateway) do
  setcode do
    Facter::Util::Resolution.exec('netstat -rn | grep "^0.0.0.0 " | cut -d " " -f10')
  end
end
